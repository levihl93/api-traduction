package tunutech.api.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateComparisonUtils {
    /**
     * Convertit java.util.Date en java.time.LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static LocalDate toLocalDatefromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.toLocalDate();
    }

    /**
     * Vérifie si la Date est AVANT ou ÉGALE à la LocalDate
     */
    public static boolean isBeforeOrEqual(Date date, LocalDate localDate) {
        if (date == null || localDate == null) return false;
        LocalDate convertedDate = toLocalDate(date);
        return convertedDate.isBefore(localDate) || convertedDate.isEqual(localDate);
    }
    public static boolean isBeforeOrEqualLocalDateTime(LocalDateTime date, LocalDate localDate) {
        if (date == null || localDate == null) return false;
        LocalDate convertedDate = toLocalDatefromLocalDateTime(date);
        return convertedDate.isBefore(localDate) || convertedDate.isEqual(localDate);
    }



    /**
     * Vérifie si la Date est APRÈS ou ÉGALE à la LocalDate
     */
    public static boolean isAfterOrEqual(Date date, LocalDate localDate) {
        if (date == null || localDate == null) return false;
        LocalDate convertedDate = toLocalDate(date);
        return convertedDate.isAfter(localDate) || convertedDate.isEqual(localDate);
    }
    public static boolean isEqual(Date date, LocalDate localDate) {
        if (date == null || localDate == null) return false;
        LocalDate convertedDate = toLocalDate(date);
        return  convertedDate.isEqual(localDate);
    }
    public static boolean isEqualLocalDateTime(LocalDateTime date, LocalDate localDate) {
        if (date == null || localDate == null) return false;
        LocalDate convertedDate = toLocalDatefromLocalDateTime(date);
        return  convertedDate.isEqual(localDate);
    }
    public static boolean isAfterOrEqualLocalDateTime(LocalDateTime date, LocalDate localDate) {
        if (date == null || localDate == null) return false;
        LocalDate convertedDate = toLocalDatefromLocalDateTime(date);
        return convertedDate.isAfter(localDate) || convertedDate.isEqual(localDate);
    }

    public static boolean isBetweenDate(Date date,LocalDate localDate1, LocalDate localDate2)
    {
        Boolean res=false;
        res=isBeforeOrEqual(date,localDate2);
        res=isAfterOrEqual(date,localDate1);
        return  res;
    }
    public static boolean isBetweenDateLocalDateTime(LocalDateTime date,LocalDate localDate1, LocalDate localDate2)
    {
        Boolean res=false;
        res=isBeforeOrEqualLocalDateTime(date,localDate2);
        res=isAfterOrEqualLocalDateTime(date,localDate1);
        return  res;
    }

}
