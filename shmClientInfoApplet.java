import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import java.net.InetAddress;
import java.net.URL;
import java.net.Socket;
import com.shm.session.shmSessionManager;
import java.net.URLConnection;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.URLEncoder;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class shmClientInfoApplet extends Applet {
    
	public shmClientInfoApplet() {}

    public void init() 
    {
        URL url;
        HttpURLConnection urlConn;
        DataOutputStream dos;
        DataInputStream dis;

        try 
        {
            String strHostName = InetAddress.getLocalHost().getHostName().toUpperCase();
            String strClientInfo = "HostName:" + strHostName.toUpperCase() + "~";
            
            com.sun.security.auth.module.NTSystem NTSystem = new com.sun.security.auth.module.NTSystem();            
            String strUserName = NTSystem.getName();
            strUserName = strUserName == null ? "NA" : strUserName;
            
            String strDomainName = NTSystem.getDomain();
            strDomainName = strDomainName == null ? "NA" : strDomainName;
            
            strClientInfo += "UserName:" + strUserName.toUpperCase() + "~";
            strClientInfo += "DomainName:" + strDomainName.toUpperCase() + "~";
            
            InetAddress[] inetAddresses = InetAddress.getAllByName(strHostName);

            String hostIPs = "";

            for (InetAddress inetAddress : inetAddresses)
                hostIPs += inetAddress.getHostAddress() + ",";
            
            hostIPs = hostIPs.substring(0, hostIPs.length() - 1);
            strClientInfo += "UserIPAddr:" + hostIPs;
            
            String strDocumentBase = this.getDocumentBase().toString();
            strDocumentBase = strDocumentBase.substring(0, strDocumentBase.lastIndexOf("/"));
            
            setBackground(new Color(242, 242, 242));
            
            url = new URL(strDocumentBase + "/SHMA/ClientInfoService/SetClientInfo?ClientInfo=" + strHostName + ";" + strClientInfo);
            url = new URI(
                   url.getProtocol(), 
                   url.getAuthority(), 
                   url.getPath(),
                   url.getQuery(), 
                   url.getRef()).
                   toURL();

            String query = "ClientInfo=" + strHostName + ";" + strClientInfo;

			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setConnectTimeout (30000);
			urlConn.setReadTimeout (30000);
			urlConn.setRequestMethod("GET");
			urlConn.setDoOutput(true);
			urlConn.connect();

			// Get result
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			
            String l = null;
            while ((l = br.readLine()) != null) {
            	System.out.println(l);
            }
            br.close();
            getAppletContext().showDocument(new URL("javascript:f_GetClientInfo(\"" + strHostName + "\")"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
