package org.example.services;

import org.example.dtos.*;
import org.example.entities.*;
import org.example.entities.keys.AttemptQuestionId;
import org.example.repositories.*;
import org.example.utils.DBUtil;
import org.example.utils.JsonUtil;
import org.example.utils.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Transactional
public class RoomService {
    Logger logger = LoggerFactory.getLogger(RoomService.class);
    public String attempt(Request request, User user) {
        try {
            AttemptExamDto attemptExamDto = MapperUtil.mapFromObject(request.getData(), AttemptExamDto.class);

            RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);
            AttemptQuestionRepository attemptQuestionRepository = DBUtil.getContext().getBean(AttemptQuestionRepository.class);

            Optional<Room> roomOptional = roomRepository.findById(attemptExamDto.getRoomId());

            if(!roomOptional.isPresent()) {
                return JsonUtil.buidResponse(new Response("error", "exam.attempt.error", "Phòng thi không tồn tại", ""));
            }

            Room room = roomOptional.get();

            // Kiểm tra xem user có được phép vào phòng thi không
            String classCode = user.getClassCode();
            if(!room.getListClassId().contains(classCode)) {
                return JsonUtil.buidResponse(new Response("error", "exam.attempt.error", "Bạn không thuộc đối tượng dự thi", ""));
            }
            Timestamp currentTime = Timestamp.from(Instant.now());
            if(currentTime.after(room.getEndTime())) {
                return JsonUtil.buidResponse(new Response("error", "exam.attempt.error", "Bài thi này đã kết thúc", ""));
            }

            if(currentTime.before(room.getStartTime())) {
                return JsonUtil.buidResponse(new Response("error", "exam.attempt.error", "Bài thi chưa diễn ra", ""));
            }

            RoomAttemptRepository roomAttemptRepository = DBUtil.getContext().getBean(RoomAttemptRepository.class);
            // Check xem đã vào thi chưa
            Optional<RoomAttempt> roomAttemptOptional = roomAttemptRepository.findFirstByRoom_RoomIdAndUserIdOrderByAttemptIdDesc(room.getRoomId(), user.getId());
            boolean isPractice = room.getIsPractice() == 1;
            RoomAttempt roomAttempt;
            // Nếu chưa thi, tạo phiên thi mới
            if(!roomAttemptOptional.isPresent()) {
                // Nếu chưa vào thi thì lưu lại lần thi này + đề thi
                Timestamp endTime = Timestamp.from(Instant.now());
                endTime.setTime(endTime.getTime() + TimeUnit.MINUTES.toMillis(room.getDuration()));

                roomAttempt = RoomAttempt.builder().room(room).user(user).attemptStatus(1).startTime(currentTime).endTime(endTime).build();
                roomAttemptRepository.save(roomAttempt);

                ArrayList<AttemptQuestion> attemptQuestions = new ArrayList<>();

                RoomAttempt finalRoomAttempt = roomAttempt;
                ArrayList<QuestionRoom> listQuestionsOfExam = new ArrayList<>(roomAttempt.getRoom().getQuestions());
                Collections.shuffle(listQuestionsOfExam);

                listQuestionsOfExam.forEach(questionRoom -> {
                    AttemptQuestion attemptQuestion = AttemptQuestion.builder().attemptQuestionId(new AttemptQuestionId(finalRoomAttempt.getAttemptId(), questionRoom.getQuestion().getQuestionId())).build();
                    attemptQuestions.add(attemptQuestion);
                });
                attemptQuestionRepository.saveAll(attemptQuestions);
            } else {
                roomAttempt = roomAttemptOptional.get();

                // Status 2 là đã hoàn thành
                if(roomAttempt.getAttemptStatus() == 2) {
                    // Nếu là chế độ luyện tập thì cho thi tiếp
                    if(!isPractice) {
                        return JsonUtil.buidResponse(new Response("error", "exam.attempt.error", "Bạn đã hoàn thành bài thi này.", ""));
                    } else {
                        // Nếu chưa vào thi thì lưu lại lần thi này + đề thi
                        Timestamp endTime = Timestamp.from(Instant.now());
                        endTime.setTime(endTime.getTime() + TimeUnit.MINUTES.toMillis(room.getDuration()));

                        roomAttempt = RoomAttempt.builder().room(room).user(user).attemptStatus(1).startTime(currentTime).endTime(endTime).build();
                        roomAttemptRepository.save(roomAttempt);
                        ArrayList<AttemptQuestion> attemptQuestions = new ArrayList<>();

                        RoomAttempt finalRoomAttempt = roomAttempt;
                        ArrayList<QuestionRoom> listQuestionsOfExam = new ArrayList<>(roomAttempt.getRoom().getQuestions());
                        Collections.shuffle(listQuestionsOfExam);

                        listQuestionsOfExam.forEach(questionRoom -> {
                            AttemptQuestion attemptQuestion = AttemptQuestion.builder().attemptQuestionId(new AttemptQuestionId(finalRoomAttempt.getAttemptId(), questionRoom.getQuestion().getQuestionId())).build();
                            attemptQuestions.add(attemptQuestion);
                        });

                        attemptQuestionRepository.saveAll(attemptQuestions);
                    }
                }

                Timestamp endAttemptTime = new Timestamp(roomAttempt.getStartTime().getTime() + TimeUnit.MINUTES.toMillis(room.getDuration()));
                if(currentTime.after(endAttemptTime) && !isPractice) {
                    roomAttempt.setAttemptStatus(2);
                    roomAttemptRepository.save(roomAttempt);
                    return JsonUtil.buidResponse(new Response("error", "exam.attempt.error", "Thời gian làm bài đã hết !", ""));
                }
            }

            // Lấy đáp án mà người dùng trả lời
            HashMap<Integer, Integer> userSelectedAnswer = new HashMap<>();
            if(roomAttempt.getAttemptQuestions() != null) {
                roomAttempt.getAttemptQuestions().forEach(attemptQuestion -> {
                    userSelectedAnswer.put(attemptQuestion.getAttemptQuestionId().getQuestionId(), attemptQuestion.getSelectedAnswerId());
                });
            }
            // Trả về danh sách câu hỏi
            AttemptExamResponseDto attemptExamResponseDto = new AttemptExamResponseDto();

            ArrayList<AttemptQuestionDto> attemptQuestionDtos = new ArrayList<>();
            roomAttempt.getRoom().getQuestions().forEach(questionRoom -> {
                ArrayList<AttemptAnswerDto> attemptAnswerDtos = (ArrayList<AttemptAnswerDto>) questionRoom.getQuestion().getAnswers().stream().map(answer -> new AttemptAnswerDto(answer.getAnswerId(), answer.getAnswerContent())).collect(Collectors.toList());
                AttemptQuestionDto attemptQuestionDto = AttemptQuestionDto.builder()
                        .questionContent(questionRoom.getQuestion().getQuestionContent())
                        .questionId(questionRoom.getQuestionRoomId().getQuestionId())
                        .answers(attemptAnswerDtos)
                        .build();
                if(userSelectedAnswer.get(questionRoom.getQuestionRoomId().getQuestionId()) != null) {
                    attemptQuestionDto.setSelectedAnswer(userSelectedAnswer.get(questionRoom.getQuestionRoomId().getQuestionId()));
                }
                attemptQuestionDtos.add(attemptQuestionDto);
            });
            attemptExamResponseDto.setQuestions(attemptQuestionDtos);
            attemptExamResponseDto.setRoomName(room.getRoomName());
            attemptExamResponseDto.setAttemptId(roomAttempt.getAttemptId());
            Timestamp durationInTimestamp = Timestamp.from(Instant.now());
            durationInTimestamp.setTime(roomAttempt.getStartTime().getTime() + TimeUnit.MINUTES.toMillis(room.getDuration()));
            attemptExamResponseDto.setDuration((int) ((durationInTimestamp.getTime() - currentTime.getTime())/ 1000));
            return JsonUtil.buidResponse(new Response("success", "exam.attempt.success", "Vào thi", attemptExamResponseDto));
        } catch(Exception e) {
            return JsonUtil.buidResponse(new Response("error", "exam.attempt.error", "Máy chủ đang bận.Hãy thử lại", null));
        }
    }

    public String list(Request request, User user) {
        RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);
        ArrayList<Room> listRoom = (ArrayList<Room>) roomRepository.findAll();
        Response response = new Response("success", "exam.attempt.listRoom", "", listRoom);
        return JsonUtil.buidResponse(response);
    }

    public String saveAttemptRequest(Request request, User user) {
        try {
            AttemptQuestionRepository attemptQuestionRepository = DBUtil.getContext().getBean(AttemptQuestionRepository.class);
            SaveAnswerAttemptDto answerAttemptDto = MapperUtil.mapFromObject(request.getData(), SaveAnswerAttemptDto.class);
            AttemptQuestion attemptQuestion = attemptQuestionRepository.getAttemptQuestionByAttemptQuestionId_AttemptIdAndAttemptQuestionId_QuestionId(answerAttemptDto.getAttemptId(), answerAttemptDto.getQuestionId());
            if(attemptQuestion != null) {
                attemptQuestion.setSelectedAnswerId(answerAttemptDto.getAnswerId());
                attemptQuestionRepository.save(attemptQuestion);
                logger.info(user.getEmail() + ": ID phòng thi: "+ answerAttemptDto.getAttemptId() + " ID câu hỏi: "+ answerAttemptDto.getQuestionId() + " ID câu trả lời: "+ answerAttemptDto.getAnswerId() +" - Lưu câu trả lời thành công");
            }
            return "";
        } catch(Exception e) {
            logger.info("Lưu câu trả lời thất bại: " + e.getMessage());
            return "";
        }
    }

    public String finishAttemptRequest(Request request, User user) {
        try {
            FinishAttemptDto finishAttemptDto = MapperUtil.mapFromObject(request.getData(), FinishAttemptDto.class);
            RoomAttemptRepository roomAttemptRepository = DBUtil.getContext().getBean(RoomAttemptRepository.class);
            RoomAttempt roomAttempt = roomAttemptRepository.findByAttemptId(finishAttemptDto.getAttemptId());
            if(roomAttempt == null) {
                return "null";
            }

            // Lấy danh sách câu hỏi - id câu trả lời trong phòng thi đó
            ArrayList<QuestionRoom> questionRoom = new ArrayList<>(roomAttempt.getRoom().getQuestions());
            HashMap<Integer, Integer> mapQuestionAnswer = new HashMap<>();
            questionRoom.forEach(question -> {
                Optional<Answer> correctAnswer = question.getQuestion().getAnswers().stream().filter(answer -> answer.isCorrect()).findFirst();
                if(correctAnswer.isPresent()) {
                    mapQuestionAnswer.put(question.getQuestionRoomId().getQuestionId(), correctAnswer.get().getAnswerId());
                }
            });

            // Lấy danh sách điểm của từng câu hỏi
            HashMap<Integer, Integer> mapQuestionScore = new HashMap<>();
            roomAttempt.getRoom().getQuestions().forEach(question -> {
                mapQuestionScore.put(question.getQuestionRoomId().getQuestionId(), question.getQuestionPoint());
            });

            // Lấy danh sách đáp án trả lời bởi user
            AtomicInteger score = new AtomicInteger(0);
            int totalScore = 0;
            AtomicInteger numberCorrectAnswer = new AtomicInteger();
            roomAttempt.getAttemptQuestions().forEach(questionAttempt -> {
                int questionId = questionAttempt.getAttemptQuestionId().getQuestionId();
                int userAnswerId = questionAttempt.getSelectedAnswerId();
                try {
                    if(mapQuestionAnswer.get(questionId) == userAnswerId) {
                        score.addAndGet(mapQuestionScore.get(questionId));
                        numberCorrectAnswer.getAndIncrement();
                    }
                } catch(Exception e) {

                }
            });

            roomAttempt.setEndTime(Timestamp.from(Instant.now()));
            roomAttempt.setAttemptStatus(2);
            roomAttempt.setScore(score.get());
            roomAttemptRepository.save(roomAttempt);

            int duration = (int) ((roomAttempt.getEndTime().getTime() -  roomAttempt.getStartTime().getTime()) / 1000);
            AttemptResultDto attemptResultDto = AttemptResultDto.builder().endTime(roomAttempt.getEndTime()).score(score.get()).totalCorrect(numberCorrectAnswer.get()).duration(duration).startTime(roomAttempt.getStartTime()).build();
            Response response = new Response("success", "exam.attempt.result", "", attemptResultDto);
            logger.info(user.getEmail() + ": ID phòng thi: "+ roomAttempt.getAttemptId() + " Tổng số điểm: "+score+" Số câu trả lời đúng: " + numberCorrectAnswer);
            return JsonUtil.buidResponse(response);
        } catch(Exception e) {
            logger.error("Chấm điểm bài thi: " + e.getMessage());
            return "";
        }

    }

    public String getHistoryAttempt(Request request, User user) {
        try {
            RoomAttemptRepository roomAttemptRepository = DBUtil.getContext().getBean(RoomAttemptRepository.class);
            ArrayList<RoomAttempt> roomAttempts = roomAttemptRepository.findAllByUserIdAndAttemptStatusOrderByAttemptIdDesc(user.getId(), 2);
            ArrayList<RoomAttemptDto> roomAttemptDtos = new ArrayList<>();
            roomAttempts.forEach(roomAttempt -> {
                RoomAttemptDto roomAttemptDto = RoomAttemptDto.builder()
                        .attemptId(roomAttempt.getAttemptId())
                        .score(roomAttempt.getScore())
                        .isPractice(roomAttempt.getRoom().getIsPractice())
                        .roomName(roomAttempt.getRoom().getRoomName())
                        .startTime(roomAttempt.getStartTime())
                        .endTime(roomAttempt.getEndTime())
                        .build();
                roomAttemptDtos.add(roomAttemptDto);
            });
            Response response = new Response("success", "exam.attempt.history", "", roomAttemptDtos);
            return JsonUtil.buidResponse(response);
        } catch(Exception e) {
            Response response = new Response("error", "exam.attempt.history.error", "Có lỗi khi lấy lịch sử thi", null);
            return JsonUtil.buidResponse(response);
        }
    }

    public String getHistoryDetail(Request request, User user) {
        try {
            GetDetailAttemptDto getDetailAttemptDto = MapperUtil.mapFromObject(request.getData(), GetDetailAttemptDto.class);
            AttemptQuestionRepository attemptQuestionRepository = DBUtil.getContext().getBean(AttemptQuestionRepository.class);
            // Lấy thông tin attempt
            RoomAttemptRepository roomAttemptRepository = DBUtil.getContext().getBean(RoomAttemptRepository.class);
            RoomAttempt roomAttempt = roomAttemptRepository.findByAttemptId(getDetailAttemptDto.getAttemptId());
            if(roomAttempt == null) {
                Response response = new Response("error", "exam.attempt.history.detail.error", "Lượt thi này không tồn tại", null);
                return JsonUtil.buidResponse(response);
            }

            // Lấy danh sách câu trả lời của user
            HashMap<Integer, Integer> userAnswer = new HashMap<>();
            ArrayList<AttemptQuestion> attemptQuestions = attemptQuestionRepository.getAllByAttemptQuestionId_AttemptId(getDetailAttemptDto.getAttemptId());
            attemptQuestions.forEach(attemptQuestion -> {
                userAnswer.put(attemptQuestion.getAttemptQuestionId().getQuestionId(), attemptQuestion.getSelectedAnswerId());
            });

            // Lấy danh sách câu hỏi và câu trả lời đúng
            HashMap<Integer, Integer> correctAnswer = new HashMap<>();
            roomAttempt.getRoom().getQuestions().forEach(questionRoom -> {
                Question question = questionRoom.getQuestion();
                question.getAnswers().forEach(answer -> {
                    if(answer.isCorrect()) {
                        correctAnswer.put(question.getQuestionId(), answer.getAnswerId());
                    }
                });

            });

            // Trả về kết quả
            AttemptHistoryDetailDto attemptExamResponseDto = new AttemptHistoryDetailDto();
            ArrayList<AttemptQuestionHistoryDto> attemptQuestionDtos = new ArrayList<>();

            roomAttempt.getRoom().getQuestions().forEach(questionRoom -> {
                try {
                    int userSelectedAnswer = userAnswer.get(questionRoom.getQuestion().getQuestionId());
                    int correctAnswerId = correctAnswer.get(questionRoom.getQuestion().getQuestionId());
                    ArrayList<AttemptAnswerDto> attemptAnswerDtos = (ArrayList<AttemptAnswerDto>) questionRoom.getQuestion().getAnswers().stream().map(answer -> new AttemptAnswerDto(answer.getAnswerId(), answer.getAnswerContent())).collect(Collectors.toList());
                    AttemptQuestionHistoryDto attemptQuestionDto = AttemptQuestionHistoryDto.builder()
                            .selectedAnswer(userSelectedAnswer)
                            .questionContent(questionRoom.getQuestion().getQuestionContent())
                            .questionId(questionRoom.getQuestionRoomId().getQuestionId())
                            .correctAnswer(correctAnswerId)
                            .answers(attemptAnswerDtos).build();
                    attemptQuestionDtos.add(attemptQuestionDto);
                } catch(Exception e) {

                }

            });
            attemptExamResponseDto.setQuestions(attemptQuestionDtos);
            attemptExamResponseDto.setRoomName(roomAttempt.getRoom().getRoomName());
            attemptExamResponseDto.setAttemptId(roomAttempt.getAttemptId());
            attemptExamResponseDto.setDuration((int) ((roomAttempt.getEndTime().getTime() - roomAttempt.getStartTime().getTime())/ 1000));
            Response response = new Response("success", "exam.history.detail", "", attemptExamResponseDto);
            return JsonUtil.buidResponse(response);

        } catch(Exception e) {
            Response response = new Response("error", "exam.history.detail.error", "Có lỗi khi lấy lịch sử bài thi", null);
            return JsonUtil.buidResponse(response);
        }


    }
}
