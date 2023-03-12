package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private Map<Group, List<User>> groupUserMap;
    private List<String> msgs;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String, String> UserMap;
//    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new TreeMap<Group, List<User>>();
        this.UserMap= new HashMap<>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
//        this.userMobile = new HashSet<>();
//        this.groupUserMap=new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) {
        if(UserMap.containsValue(mobile)){
            try {
                throw new Exception("User already exists");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else{UserMap.put(name,mobile);}
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        //if grp size is 2
        Group newGroup = new Group();
        if(users.size()==2){
            String groupName =users.get(1).getName();
            newGroup=new Group(groupName,2);
        }
        //if grp size is greater than 2
        else if(users.size()>2){
            customGroupCount++;
            newGroup=new Group("Group "+customGroupCount,users.size());
        }
        groupUserMap.put(newGroup,users);
        adminMap.put(newGroup,users.get(0));
        return  newGroup;
    }

    public int createMessage(String content) {
        msgs.add(content);
        messageId++;
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) {
        if(!groupUserMap.containsKey(group)){
            try {
                throw new Exception("Group does not exist");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        List<User> users= groupUserMap.get(group);
        boolean flag = false;
        for(User currUser: users){
            if(currUser==sender){
                flag=true;
                break;
            }
        }
        if(!flag){
            try {
                throw new Exception("You are not allowed to send message");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        List<Message> messg= new ArrayList<>();
        if(groupMessageMap.containsKey(group)) messg=groupMessageMap.get(group);
        messg.add(message);
        groupMessageMap.put(group,messg);

        senderMap.put(message,sender);
        return messg.size();
    }

    public String changeAdmin(User approver, User user, Group group) {
        if(!groupUserMap.containsKey(group)){
            try {
                throw new Exception("Group does not exist");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if(!adminMap.get(group).equals(approver)){
            try {
                throw new Exception("Approver does not have rights");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        List<User> users= groupUserMap.get(group);
        boolean flag = false;
        for(User currUser: users){
            if(currUser==user){
                flag=true;
                break;
            }
        }
        if(!flag){
            try {
                throw new Exception("User is not a participant");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        adminMap.remove(group);
        adminMap.put(group,user);
        return "SUCCESS";
    }

    public int removeUser(User user) {
        boolean flag = false;
        for(Group grp: groupUserMap.keySet()){
            if(groupUserMap.get(grp).contains(user)){
                flag=true;
            }
        }
        if(!flag){
            try {
                throw new Exception("User not found");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Collection<User> Admins = adminMap.values();
        //boolean isAdmin= false;
        for(User admin : Admins){
            if(admin==user){
                try {
                    throw new Exception("Cannot remove admin");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Group curgrp = new Group();
        for(Group grp: groupUserMap.keySet()){
            if(groupUserMap.get(grp).contains(user)){
                curgrp=grp;
            }
        }
        groupUserMap.get(curgrp).remove(user);
        for(Message msg : senderMap.keySet()){
            if(senderMap.get(msg).equals(user)){
                for (Group grp : groupMessageMap.keySet()){
                    if(groupMessageMap.get(grp).contains(msg)){
                        groupMessageMap.remove(msg);
                    }
                }
                senderMap.remove(msg);
                msgs.remove(msg);
            }
        }


        int curgrpSize=groupUserMap.get(curgrp).size();
        int curMessagesinGrp=groupMessageMap.get(curgrp).size();
        int allmessages= msgs.size();
        return curgrpSize+curMessagesinGrp+allmessages;
    }

//    public String findMessage(Date start, Date end, int k) {
//
//    }
}
