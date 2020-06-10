package com.ezpass.smopaye_mobile.Manage_Transactions_Story;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * kudos: http://helpdesk.objects.com.au/java/how-can-i-iterate-through-all-dates-in-a-range
 * @date   Jan 12, 2010
 */
public class DateIterator implements Iterator<Date>, Iterable<Date> {
    private Calendar end = Calendar.getInstance();
    private Calendar current = Calendar.getInstance();

    public DateIterator(Date start, Date end)
    {
        this.end.setTime(end);
        this.end.add(Calendar.DATE, -1);
        this.current.setTime(start);
        this.current.add(Calendar.DATE, -30);
    }

    // le constructeur
    /*public DateIterator(Date start)
    {
        this.current.setTime(start);
        this.end.setTime(start);
        this.end.add(Calendar.DATE, 7);
    }*/

    public boolean hasNext()
    {
        return !current.after(end);
    }

    public Date next()
    {
        current.add(Calendar.DATE, 1);
        return current.getTime();
    }

    public void remove()
    {
        throw new UnsupportedOperationException(
                "Cannot remove");
    }

    public Iterator<Date> iterator()
    {
        return this;
    }

    /*public static void main(String[] args)
    {
        //Map<String, String>  mapValues = theform.getColumn();

        //String newColumnValues[] = new ArrayList<String>(mapValues.values()).toArray(new String[0]);

        Date d1 = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 20);
        Date d2 = cal.getTime();

        Iterator<Date> i = new DateIterator(d1, d2);
        while(i.hasNext())
        {
            Date date = i.next();
            System.out.println(date);
        }
    }*/

}
