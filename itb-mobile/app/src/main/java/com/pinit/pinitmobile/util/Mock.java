package com.pinit.pinitmobile.util;


import com.pinit.pinitmobile.App;
import com.pinit.pinitmobile.Globals;
import com.pinit.pinitmobile.model.Comment;
import com.pinit.pinitmobile.model.Event;
import com.pinit.pinitmobile.model.Group;
import com.pinit.pinitmobile.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Mock {

    public static List<Event> generateEvents() {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Event e = new Event();
            Random generator = new Random();
            float maxX = (float) 0.05;
            float minX = (float) -0.04;
            float latRand = generator.nextFloat() * (maxX - minX) + minX;
            float lonRand = generator.nextFloat() * (maxX - minX) + minX;
            e.setEventId((long) i);
            e.setDescription("Tu jest opis wydarzenia....\nOpis, opis, opis\nI dalsza część opisu...." + String.valueOf(i));
            e.setPositivesAmount(10 + i);
            e.setNegativesAmount(7 + i);
            e.setName("Wydarzenie" + String.valueOf(i));
            e.setEventTypeId((long) i);
            events.add(e);
        }
        return events;
    }

    public static List<Group> generateGroups() {
        List<Group> groups = new ArrayList<>();
        List<Event> events = App.getEventsDao().getAll();
        for (int i = 0; i < 10; i++) {
            Group g = new Group();
            g.setGroupId((long) i);
            g.setName("Grupa" + String.valueOf(i));
            if(i<5) {
                g.addEvent(events.get(i));
               // events.get(i).setGroup(g);
            }
            g.setAdminId((long) (i % 2));
            groups.add(g);
        }
        return groups;
    }

    public static void generateTypes() {
        List<String> types = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            types.add("Typ" + String.valueOf(i));
        }
        UserPreferences.saveCollection(types, Globals.EVENT_TYPE);
    }

    public static List<Comment> generateComments() {
        List<Comment> comments = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 5; i++) {
            User a = new User();
            a.setEmail("mail@mail");
            a.setUsername("UserMock" + String.valueOf(i));
            Comment c = new Comment();
            c.setAuthor(a);
            c.setDate(calendar.get(Calendar.MILLISECOND));
            c.setComment("Komentarz, mniej lub bardzie treściwy\n Do 160 znaków" + String.valueOf(i));
            comments.add(c);
        }
        return comments;
    }

    public static User generateUser() {
        User u = new User();
        u.setUserId((long) 1);
        u.setUsername("rukkia");
        u.setPhoneNumber("509770726");
        u.setFirstName("Agnieszka");
        u.setLastName("Ciborowska");
        u.setCommentsAmount(10);
        u.setPositivesAmount(64);
        u.setNegativesAmount(32);
        u.setEmail("aga.ciborowska@gmail.com");
        return u;
    }

    public static List<User> generateUsers() {
        List<User> users = new ArrayList<>();
        for(int i=0;i<10;i++){
            User u = new User();
            u.setEmail("User"+String.valueOf(i)+"@mock.com");
            u.setUsername("User"+String.valueOf(i));
            u.setUserId((long) i);
            users.add(u);
        }
        return users;
    }
}