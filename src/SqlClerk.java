import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;

public class SqlClerk extends Thread{

    private Queue<Container> threadSafeQueue;
    private Connection connection;
    private boolean flag;
    private PreparedStatement ps;

    public SqlClerk(Queue<Container> threadSafeQueue) {
        this.threadSafeQueue = threadSafeQueue;
        this.connection = ConnectionManager.getConnection();
        this.flag = true;
    }

    public void run(){
        try {
            ps = connection.prepareStatement("INSERT INTO Graphs10 VALUES (?, ?, ?, ?)");
        while (flag || !threadSafeQueue.isEmpty()) {
            int size = threadSafeQueue.size();
            while (size-- > 0) {
                Container container = threadSafeQueue.poll();
                ps.setString(1, container.getGraph());
                ps.setInt(2, container.getGeo());
                ps.setInt(3, container.getIndep());
                ps.setInt(4, container.getDep());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.clearBatch();
            Thread.sleep(1000);
        }
        } catch (SQLException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage() + e.getStackTrace()[0].toString());
            System.exit(1);
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
