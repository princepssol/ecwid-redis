package com.ecwid.app;

import com.ecwid.app.redis.util.collection.RedisList;

import java.util.List;
import java.util.ListIterator;

public class App
{
    public static void main( String[] args ) {
        List<Integer> list = new RedisList(List.of(1,1,2,2,3,3));

        ListIterator<Integer> it = list.listIterator();
        while (it.hasNext()) {
            Integer i = it.next();
            it.remove();
        }

        list.clear();
    }
}
