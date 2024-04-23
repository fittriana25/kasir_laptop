
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class koneksi {
    private String url="jdbc:mysql://localhost/apk_kasirfittriana";
    private String username="root";
    private String password;
    private Connection con;
    
    public void connec(){
        try {
            con = DriverManager.getConnection(url,username,password);
            System.out.println("koneksi berhasil");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "koneksi gagal");
        }
    }
    public Connection getCon(){
        return con;
    }

}
