package net.oldschoolminecraft.bs;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;

public class PTUtil
{
    private static Gson gson = new Gson();

    public static long getPlaytimeHours(String username)
    {
        try (FileReader reader = new FileReader("plugins/OSM-Ess/player-logs/" + username.toLowerCase() + ".json"))
        {
            return TimeUnit.MILLISECONDS.toHours(gson.fromJson(reader, OSMPLUserData.class).playTime);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return 0;
        }
    }
}
