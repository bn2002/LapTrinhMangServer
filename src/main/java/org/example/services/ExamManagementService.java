package org.example.services;

import org.example.dtos.*;
import org.example.entities.*;
import org.example.entities.keys.QuestionRoomId;
import org.example.repositories.*;
import org.example.utils.DBUtil;
import org.example.utils.JsonUtil;
import org.example.utils.MapperUtil;
import org.example.utils.ValidationUtil;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExamManagementService {
    Logger logger = LoggerFactory.getLogger(ExamManagementService.class);
    @Transactional
    public String create(Request request, User user) {
        try {
            RoomDto roomDto = MapperUtil.mapFromObject(request.getData(), RoomDto.class);
            ArrayList<InputErrorDto> errors = ValidationUtil.runValidation(roomDto);
            if(errors.size() > 0) {
                return JsonUtil.buidResponse(new Response("error","exam.create.validation_error", "Thông tin bạn nhập không hợp lệ", errors));
            }

            Room room = MapperUtil.mapFromObject(roomDto, Room.class);
            room.setOwnerId(user.getId());
            room.getQuestions().forEach(questionRoom -> {
                questionRoom.getQuestion().setOwnerId(user.getId());
                questionRoom.setRoom(room);
                questionRoom.getQuestion().getAnswers().forEach(tempQuestion -> {
                    tempQuestion.setQuestion(questionRoom.getQuestion());
                });
                questionRoom.setQuestionRoomId(new QuestionRoomId(questionRoom.getQuestion().getQuestionId(), room.getRoomId()));
                questionRoom.setQuestionPoint(questionRoom.getQuestion().getScore());
            });
            RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);

            roomRepository.saveAndFlush(room);
            logger.info("Tạo phòng thi thành công");
            return JsonUtil.buidResponse(new Response("success", "exam.create.success", "Tạo phòng thi thành công", ""));

        } catch(Exception e) {
            logger.info("Tạo phòng thi: " + e.getMessage());
            return JsonUtil.buidResponse(new Response("error", "exam.create.unknown_error", "Có lỗi không xác định đã xảy ra khi tạo phòng thi, hãy thử lại", ""));
        }

    }

    public String list(Request request, User user) {
        RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);
        ArrayList<Room> listRoom = roomRepository.findAllByOwnerId(user.getId());
        Response response = new Response("success", "exam.list_room", "", listRoom);
        logger.info("Lấy danh sách phòng thi thành công");
        return JsonUtil.buidResponse(response);
    }

    public String getRoomQuestion(Request request, User user) {
       try {
            AttemptExamDto attemptExamDto = MapperUtil.mapFromObject(request.getData(), AttemptExamDto.class);
            RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);
            Optional<Room> roomOptional = roomRepository.findById(attemptExamDto.getRoomId());
            if(!roomOptional.isPresent()) {
               return JsonUtil.buidResponse(new Response("error", "exam.management.error", "Phòng thi không tồn tại", ""));
            }

            Room room = roomOptional.get();
            HashMap<Integer, Integer> questionPoint = new HashMap<Integer, Integer>();
            List<Question> listQuestion = room.getQuestions().stream().map(questionRoom -> {
                questionPoint.put(questionRoom.getQuestion().getQuestionId(), questionRoom.getQuestionPoint());
                return questionRoom.getQuestion();
            }).collect(Collectors.toList());
            ArrayList<EditQuestionDto> editQuestionDtos = MapperUtil.mapFromObject(listQuestion, new TypeToken<ArrayList<EditQuestionDto>>(){}.getType());
            editQuestionDtos.forEach(editQuestionDto -> editQuestionDto.setScore(questionPoint.get(editQuestionDto.getQuestionId())));
            Response response = new Response("success", "exam.management.get_question", "", editQuestionDtos);
            return JsonUtil.buidResponse(response);
       } catch (Exception e) {
            return JsonUtil.buidResponse(new Response("error", "exam.management.get_question_error", "Có lỗi khi lấy danh sách câu hỏi của phòng thi này", ""));
       }
    }

    public String updateRoom(Request request, User user) {
        try {
            EditRoomDto editRoomDto = MapperUtil.mapFromObject(request.getData(), EditRoomDto.class);
            RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);
            QuestionRoomRepository questionRoomRepository = DBUtil.getContext().getBean(QuestionRoomRepository.class);
            AttemptQuestionRepository attemptQuestionRepository = DBUtil.getContext().getBean(AttemptQuestionRepository.class);
            if(editRoomDto.getDeletedQuestionId().size() > 0) {
                questionRoomRepository.deleteByQuestionId(editRoomDto.getDeletedQuestionId());
                attemptQuestionRepository.deleteByQuestionId(editRoomDto.getDeletedQuestionId());
            }
            Optional<Room> roomOptional = roomRepository.findById(editRoomDto.getRoomId());
            if(!roomOptional.isPresent()) {
                return JsonUtil.buidResponse(new Response("error", "exam.management.update_error", "Phòng thi này không tồn tại", ""));
            }

            // Cập nhật các thông tin cơ bản
            Room room = roomOptional.get();
            room.setRoomName(editRoomDto.getRoomName());

            room.setDuration(editRoomDto.getDuration());
            room.setIsPractice(editRoomDto.getIsPractice());
            room.setListClassId(editRoomDto.getListClassId());
            room.setStartTime(editRoomDto.getStartTime());
            room.setEndTime(editRoomDto.getEndTime());

            ArrayList<EditQuestionDto> editQuestionDtos = editRoomDto.getQuestions();
            room.setCountQuestion(editQuestionDtos.size());

            // Cập nhật câu hỏi
            HashMap<Integer, Integer> questionScore = new HashMap<>();
            HashMap<Integer, String> questionContent = new HashMap<>();

            HashMap<Integer, String>  answerContent = new HashMap<>();
            HashMap<Integer, Boolean> answerIsCorrect = new HashMap<>();

            for(EditQuestionDto questionDto: editQuestionDtos) {
                if(questionDto.getQuestionId() == 0) {
                    Question newQuestion = MapperUtil.mapFromObject(questionDto, Question.class);
                    newQuestion.setOwnerId(user.getId());
                    newQuestion.getAnswers().forEach(tempQuestion -> {
                        tempQuestion.setQuestion(newQuestion);
                    });
                    QuestionRoom questionRoom = new QuestionRoom(newQuestion, room);
                    questionRoom.setQuestionPoint(questionDto.getScore());
                    room.getQuestions().add(questionRoom);
                } else {
                    questionScore.put(questionDto.getQuestionId(), questionDto.getScore());
                    questionContent.put(questionDto.getQuestionId(), questionDto.getQuestionContent());
                    for(EditAnswerDto answerDto: questionDto.getAnswers()) {
                        // Trường hợp cập nhật các câu hỏi cũ
                        if(answerDto.getAnswerId() > 0) {
                            answerContent.put(answerDto.getAnswerId(), answerDto.getAnswerContent());
                            answerIsCorrect.put(answerDto.getAnswerId(), answerDto.isCorrect());
                        }
                    }
                }
            }

            // Cập nhật lại cau hoi, dap an
            room.getQuestions().forEach(questionRoom -> {
                int questionId = questionRoom.getQuestion().getQuestionId();
                if(questionId > 0) {
                    questionRoom.getQuestion().setQuestionContent(questionContent.get(questionId));
                    questionRoom.setQuestionPoint(questionScore.get(questionId));
                    // Cập nhật câu trả lời
                    questionRoom.getQuestion().getAnswers().forEach(answer -> {
                        int answerId = answer.getAnswerId();
                        answer.setAnswerContent(answerContent.get(answerId));
                        answer.setCorrect(answerIsCorrect.get(answerId));
                    });
                }
            });

            // Lưu thông tin phòng
            roomRepository.save(room);

            Response response = new Response("success", "exam.management.updateExam", "Cập nhật phòng thi thành công", null);
            return JsonUtil.buidResponse(response);
        } catch(Exception e) {
            Response response = new Response("error", "exam.management.updateExam", "Có lỗi trong quá trình cập nhật phòng thi", null);
            return JsonUtil.buidResponse(response);
        }
    }

    public String deleteExam(Request request, User user) {
        try {
            EditRoomDto editRoomDto = MapperUtil.mapFromObject(request.getData(), EditRoomDto.class);
            RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);
            QuestionRoomRepository questionRoomRepository = DBUtil.getContext().getBean(QuestionRoomRepository.class);
            AttemptQuestionRepository attemptQuestionRepository = DBUtil.getContext().getBean(AttemptQuestionRepository.class);
            RoomAttemptRepository roomAttemptRepository = DBUtil.getContext().getBean(RoomAttemptRepository.class);

            Optional<Room> roomOptional = roomRepository.findById(editRoomDto.getRoomId());
            if(!roomOptional.isPresent()) {
                return JsonUtil.buidResponse(new Response("error", "exam.management.deleteExam", "Phòng thi này không tồn tại", ""));
            }

            int roomId = editRoomDto.getRoomId();
            ArrayList<Integer> listAttemptId = roomAttemptRepository.getAttemptByRoomId(roomId);
            if(listAttemptId.size() > 0) {
                roomAttemptRepository.deleteByRoomId(roomId);
                attemptQuestionRepository.deleteByAttemptId(listAttemptId);
            }

            questionRoomRepository.deleteByQuestionRoomId_RoomId(roomId);
            roomRepository.deleteById(roomId);
            Response response = new Response("success", "exam.management.deleteExam", "Xóa phòng thi thành công", null);
            return JsonUtil.buidResponse(response);
        } catch(Exception e) {
            Response response = new Response("error", "exam.management.deleteExam", "Xóa phòng thi thất bại", null);
            return JsonUtil.buidResponse(response);
        }
    }

    public String statisticExam(Request request, User user) {
        try {
            EditRoomDto editRoomDto = MapperUtil.mapFromObject(request.getData(), EditRoomDto.class);
            RoomAttemptRepository roomAttemptRepository = DBUtil.getContext().getBean(RoomAttemptRepository.class);
            ArrayList<RoomAttempt> roomAttempts = roomAttemptRepository.getByRoom_RoomId(editRoomDto.getRoomId());
            Response response = new Response("success", "exam.management.statisticExam.success", "", roomAttempts);
            return JsonUtil.buidResponse(response);
        } catch(Exception e) {
            Response response = new Response("error", "exam.management.statisticExam.error", "Có lỗi trong quá trình xem thống kê phòng thi", null);
            return JsonUtil.buidResponse(response);
        }
    }

    public String histogramExam(Request request, User user) {
        try {
            EditRoomDto editRoomDto = MapperUtil.mapFromObject(request.getData(), EditRoomDto.class);
            RoomAttemptRepository roomAttemptRepository = DBUtil.getContext().getBean(RoomAttemptRepository.class);
            ArrayList<Object> roomAttempts = roomAttemptRepository.getHistogramChart(editRoomDto.getRoomId());
            Response response = new Response("success", "exam.management.histogramExam.success", "", roomAttempts);
            return JsonUtil.buidResponse(response);
        } catch(Exception e) {
            Response response = new Response("error", "exam.management.histogramExam.error", "Có lỗi trong quá trình lấy thông tin biểu đồ", null);
            return JsonUtil.buidResponse(response);
        }
    }
}
