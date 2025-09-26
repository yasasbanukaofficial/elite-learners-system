package lk.ijse.learners.bo.context;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.HashMap;
import java.util.Map;

public class RefreshContext {
    private static final RefreshContext instance = new RefreshContext();
    private final Map<TableName, BooleanProperty> refreshFlags = new HashMap<>();

    private RefreshContext() {
        for (TableName table : TableName.values()) {
            refreshFlags.put(table, new SimpleBooleanProperty(false));
        }
    }

    public static RefreshContext getInstance() {
        return instance;
    }

    public void setRefreshFlag(TableName tableName, boolean value) {
        BooleanProperty flag = refreshFlags.get(tableName);
        if (flag != null) {
            flag.set(value);
        }
    }

    public BooleanProperty getRefreshFlag(TableName tableName) {
        return refreshFlags.get(tableName);
    }

    public enum TableName {
        DASHBOARD, STUDENT, COURSES, LESSONS, USERS, INSTRUCTORS, STUDENT_ENROLLED_LIST, INSTRUCTOR_ENROLLED_LIST, COURSES_ENROLLED_LIST, COURSES_ASSIGNED_LIST;
    }
}