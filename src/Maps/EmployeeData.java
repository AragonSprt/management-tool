package Maps;

public class EmployeeData {
    private final String id;
    private final int salary;

    public EmployeeData(String id, int salary) {
        this.id = id;
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public int getSalary() {
        return salary;
    }
}
