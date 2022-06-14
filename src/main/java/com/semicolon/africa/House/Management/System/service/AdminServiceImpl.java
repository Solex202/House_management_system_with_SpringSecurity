package com.semicolon.africa.House.Management.System.service;


import com.semicolon.africa.House.Management.System.data.models.Gender;
import com.semicolon.africa.House.Management.System.data.models.Room;
import com.semicolon.africa.House.Management.System.data.models.User;
import com.semicolon.africa.House.Management.System.data.repository.RoomRepository;
import com.semicolon.africa.House.Management.System.data.repository.UserRepository;
import com.semicolon.africa.House.Management.System.dtos.request.AssignRoomRequest;
import com.semicolon.africa.House.Management.System.dtos.request.BookRoomRequest;
import com.semicolon.africa.House.Management.System.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

//    @Autowired
//    public AdminServiceImpl(UserRepository userRepository, RoomRepository roomRepository){
//        this.userRepository = userRepository;
//        this.roomRepository = roomRepository;
//    }

    @Override
    public String assignRoom(AssignRoomRequest assignRoomRequest) {

        User user = userRepository.findByEmail(assignRoomRequest.getNewOccupantEmail()).orElseThrow(()-> new UserNotFoundException("user not found"));

        if(assignRoomRequest.getRoom().getRoomNumber() > 60 || assignRoomRequest.getRoom().getRoomNumber() < 1){
            throw new RoomNumberDoesNotExistException("room " + assignRoomRequest.getRoom().getRoomNumber() +" not available");
        }

        if(user.getGender().equals(Gender.FEMALE) && (assignRoomRequest.getRoom().getRoomNumber() > 30 || assignRoomRequest.getRoom().getRoomNumber() < 1)){

            throw new MaleWingException("cannot add "+ user.getEmail()+" to male wing");
        }
        if(user.getGender().equals(Gender.MALE) && (assignRoomRequest.getRoom().getRoomNumber() < 30 || assignRoomRequest.getRoom().getRoomNumber() > 60)){
            throw new FemaleWingException("cannot add "+user.getEmail()+"  to female wing");
        }
//        if(!user.isMadePayment()){
//            throw new PaymentException( user.getEmail() +" has not made payment");
//        }

        Room room = new Room();
        room.setEmail(user.getEmail());
        room.setRoomNumber(assignRoomRequest.getRoom().getRoomNumber());
        room.setRoomType(assignRoomRequest.getRoom().getRoomType());

        roomRepository.save(room);

        return "room successfully assigned";
    }

    @Override
    public void evictTenant(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("user not found"));
        userRepository.delete(user);

    }
}
