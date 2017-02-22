import java.util.*;
import java.io.*;

class Person
{
    // 7 days, 5 times per day
    int[][] preferences;
    String name;

    public Person(String name)
    {
        this.name = name;
        preferences = new int[7][5];
    }
}

public class InputSchedule
{
    String filename;
    public static ArrayList<Person> people;

    public InputSchedule(String filename)
    {
        this.filename = filename;    
        people = new ArrayList<>();

    }

    public void read_preferences() throws FileNotFoundException
    {
        Scanner in = new Scanner(new File(filename));

        for (int i = 0; i < 7; i++)
        {
            String name = in.next();
            Person p = new Person(name);

            for (int time = 0; time < 5; time++)
            {
                for (int day = 0; day < 7; day++)
                {
                    p.preferences[day][time] = in.nextInt();
                }
            }

            people.add(p);
        }
    }
}