package org.example.services;

import org.example.dtos.AttemptExamDto;
import org.example.dtos.AttemptExamResponseDto;
import org.example.entities.*;
import org.example.entities.keys.AttemptQuestionId;
import org.example.repositories.*;
import org.example.utils.DBUtil;
import org.example.utils.JsonUtil;
import org.example.utils.MapperUtil;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RoomService {
    public String attempt(Request request, User user) {
        AttemptExamDto attemptExamDto = MapperUtil.mapFromObject(request.getData(), AttemptExamDto.class);
        RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);
        Optional<Room> roomOptional = roomRepository.findById(attemptExamDto.getRoomId());
        if(!roomOptional.isPresent()) {
            return JsonUtil.buidResponse(new Response("error", "exam.attempt.error", "Phòng thi không tồn tại", ""));
        }
        Room room = roomOptional.get();
        Timestamp currentTime = Timestamp.from(Instant.now());
        if(currentTime.after(room.getEndTime())) {
            return JsonUtil.buidResponse(new Response("error", "exam.attempt.error", "Bài thi này đã kết thúc", ""));
        }

        if(currentTime.before(room.getStartTime())) {
            return JsonUtil.buidResponse(new Response("error", "exam.attempt.error", "Bài thi chưa diễn ra", ""));
        }
        RoomAttemptRepository roomAttemptRepository = DBUtil.getContext().getBean(RoomAttemptRepository.class);
        // Check xem đã vào thi chưa
        Optional<RoomAttempt> roomAttemptOptional = roomAttemptRepository.findByRoomIdAndUserId(room.getRoomId(), user.getUserId());
        boolean isPractice = room.getIsPractice() == 0;
        // Nếu đã vào thi
        if(roomAttemptOptional.isPresent()) {
            // Status 2 là đã hoàn thành
            if(roomAttemptOptional.get().getAttemptStatus() == 2) {
                // Nếu là chế độ luyện tập thì cho thi tiếp
                if(isPractice) {

                } else {
                    return JsonUtil.buidResponse(new Response("error", "exam.attempt.error", "Bạn đã hoàn thành bài thi này.", ""));
                }
            } else if(roomAttemptOptional.get().getAttemptStatus() == 1) {

            }
        }

        // Nếu chưa vào thi thì lưu lại lần thi này + đề thi
        Timestamp endTime = Timestamp.from(Instant.now());
        endTime.setTime(endTime.getTime() + TimeUnit.MINUTES.toMillis(room.getDuration()));
        RoomAttempt roomAttempt = RoomAttempt.builder().roomId(room.getRoomId()).userId(user.getUserId()).attemptStatus(1).startTime(currentTime).endTime(endTime).build();
        roomAttemptRepository.save(roomAttempt);

        // Lấy danh sách ID câu hỏi của phòng thi đó
        QuestionRoomRepository questionRoomRepository = DBUtil.getContext().getBean(QuestionRoomRepository.class);
        ArrayList<QuestionRoom> listQuestionInRoom = questionRoomRepository.findAllByRoom(room);
        Collections.shuffle(listQuestionInRoom);
        ArrayList<AttemptQuestion> attemptQuestions = new ArrayList<>();
        ArrayList<Integer> listQuestionId = new ArrayList<>();
        listQuestionInRoom.forEach(question -> {
            AttemptQuestion temp = AttemptQuestion.builder().attemptQuestionId(new AttemptQuestionId(roomAttempt.getAttemptId(), question.getQuestion().getQuestionId())).build();
            attemptQuestions.add(temp);
            listQuestionId.add(question.getQuestion().getQuestionId());
        });

        AttemptQuestionRepository attemptQuestionRepository = DBUtil.getContext().getBean(AttemptQuestionRepository.class);
        attemptQuestionRepository.saveAll(attemptQuestions);

        AttemptExamResponseDto attemptExamResponseDto = new AttemptExamResponseDto();
        QuestionRepository questionRepository = DBUtil.getContext().getBean(QuestionRepository.class);
        attemptExamResponseDto.setQuestions(questionRepository.findAllByQuestionIdIn(listQuestionId));

        attemptExamResponseDto.setRoomName(room.getRoomName());
        attemptExamResponseDto.setDuration(room.getDuration());
        return JsonUtil.buidResponse(new Response("success", "exam.attempt.success", "Vào thi", attemptExamResponseDto));
    }

    @Transactional
    public String list(Request request, User user) {
        RoomRepository roomRepository = DBUtil.getContext().getBean(RoomRepository.class);
        ArrayList<Room> listRoom = (ArrayList<Room>) roomRepository.findAll();
        Response response = new Response("success", "exam.attempt.listRoom", "", listRoom);
        return JsonUtil.buidResponse(response);
    }
}
