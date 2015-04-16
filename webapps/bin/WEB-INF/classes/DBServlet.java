import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;

import org.json.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

import java.security.SecureRandom;
import java.math.BigInteger;

public class DBServlet extends HttpServlet {

    private static final long serialVersionUID = -3388076538168097844L;
    
    private Connection conn;
    
    public void initDB() throws SQLException, IllegalAccessException, InstantiationException, ClassNotFoundException
    {
        String url = "jdbc:mysql://hbgwebfe.hbg.psu.edu/nebulock";
        String driver = "com.mysql.jdbc.Driver";
        String userName = "rmo5087";
        String password = "4636";
        
        Class.forName(driver).newInstance();
        conn = DriverManager.getConnection(url,userName,password);
    }

    public DBServlet() throws IllegalAccessException, InstantiationException, ClassNotFoundException, SQLException
    {
        initDB();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        // SECURITY WARNING:  In order to protect the confidentiality of the password, this communication 
        // should occur over an encrypted https connection, using additional parameters that establish the user's identity, AND
        // using POST not get

        // For demonstration purposes, we will submit all requests using the Get method

        resp.setBufferSize(8 * 1024); // 8K buffer
        resp.setContentType("text/html");

				System.out.println(req.getRequestURI());
            
        if (req.getRequestURI().equalsIgnoreCase("/bin/getAccount"))
        {
             doGetAccount(req,resp);
        }
        else if (req.getRequestURI().equalsIgnoreCase("/bin/createAccount"))
        {
             doCreateAccount(req,resp);
        }
        /*else if (req.getRequestURI().equalsIgnoreCase("/bin/updateAccount"))
        {
             doUpdateAccount(req,resp);
        }*/
        /*else if (req.getRequestURI().equalsIgnoreCase("/bin/deleteAccount"))
        {
             doDeleteAccount(req,resp);
        }*/
        else if (req.getRequestURI().equalsIgnoreCase("/bin/login")){
             doLogin(req,resp);
        }
        else if (req.getRequestURI().equalsIgnoreCase("/bin/doCreateVault")){
             doCreateVault(req,resp);
        }
        else if (req.getRequestURI().equalsIgnoreCase("/bin/doGetVaults")){
             doGetVaults(req,resp);
        }
        else if ( req.getRequestURI().equalsIgnoreCase("/bin/doDeleteVault")){
             doDeleteVault(req,resp);
        }
        else if ( req.getRequestURI().equalsIgnoreCase("/bin/doEditVault")){
             doEditVault(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doGetEntries")){
             doGetEntries(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doCreateEntry")){
             doCreateEntry(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doDeleteEntry")){
             doDeleteEntry(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doEditEntry")){
             doEditEntry(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doGetContacts")){
             doGetContacts(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doCreateContact")){
             doCreateContact(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doEditContact")){
             doEditContact(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doDeleteContact")){
             doDeleteContact(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doGetDelegatedVaults")){
             doGetDelegatedVaults(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doCreateDelegate")){
             doCreateDelegate(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doGetDelegates")){
             doGetDelegates(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doEditDelegate")){
             doEditDelegate(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doDeleteDelegate")){
             doDeleteDelegate(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doShareVault")){
             doShareVault(req,resp);
        }
        else if(req.getRequestURI().equalsIgnoreCase("/bin/doCreateKey")){
             doCreateKey(req,resp);
        }
        else {
             resp.getWriter().println("INVALID REQUEST");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
    }

    private void doGetAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String query = "SELECT email FROM Accounts "; 
            String where = "";
            if (req.getParameter("email") != null) {
                where = " WHERE email = ?";
            }

            PreparedStatement statement = conn.prepareStatement(query + where);

            // Debugging
            System.out.println(req.getParameter("email"));
            System.out.println(query + where);

            if (req.getParameter("email") != null) { 
                statement.setString(1,req.getParameter("email"));
            }

            ResultSet resultSet = statement.executeQuery();
            StringBuffer sb = new StringBuffer();
            sb.append("{");
            sb.append("  \"result\":\"success\",");
            sb.append("  \"records\":[");
            boolean firstRec = true;
            while (resultSet.next()) //TODO: This is where we encode the table result (one loop for each row)
            {
                if (firstRec){
                  firstRec = false;
                }
                else{
                  sb.append("              ,");
                }
                //sb.append("              { \"accountID\":" + resultSet.getInt("accountID") + ",");
                sb.append("{\"email\":\"" + resultSet.getString("email") + "\"}");
            }
            sb.append("]");
            sb.append("}");
            // Debugging
            System.out.println(sb);
            out.println(sb);
       }
        catch (SQLException e)
        {
            out.println("{\"result\":\"failure\"}");
            System.err.println(e);
            e.printStackTrace();
        }
    }

    private void doCreateAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            String salt = nextSessionId();
            System.out.println("Salt for " + email + ": " + salt);
						System.out.println("Hash for " + email + ": " + new String(getHash(password + salt)));
						System.out.println("pwd for " + email + ": " + password);

            PreparedStatement statement = conn.prepareStatement("INSERT INTO Accounts (email, pwdhash, salt) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,req.getParameter("email"));
            statement.setString(2,new String(getHash(password + salt)));
            statement.setString(3,salt);

            int updated = statement.executeUpdate();
            if (updated == 1){
                JSONObject retval = new JSONObject();
                try(ResultSet genKeys = statement.getGeneratedKeys()){
                  if(genKeys.next()){
                    retval.put("accountID", genKeys.getInt(1));
                  }else{
                    throw new SQLException("Creating Account failed, no ID obtained");
                  }
                  sendSuccess(out, retval);
                }
            }
            else{
                sendFailure(out, "Create account failed", null);
            }
        }
        catch (Exception e)
        {
            sendError(out, "Unable to process your request. Please try again later!", null);
            System.err.println(e);
            e.printStackTrace();
        }

    }

private void doLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String salt = "";

            PreparedStatement saltStatement = conn.prepareStatement("SELECT salt FROM Accounts WHERE email = ?");
            saltStatement.setString(1,req.getParameter("email"));
            ResultSet saltResultSet = saltStatement.executeQuery();

            if(saltResultSet.first()){
                salt = saltResultSet.getString("salt");
                System.out.println(salt);
            } else{
                //debugging
                sendFailure(out, "NO SUCH ACCOUNT", null);
                return;
            }

            //debugging
            System.out.println("Salt: " + salt);

            String pwdhash = new String(getHash(password + salt));
            System.out.println("Hash: " + pwdhash);
						System.out.println("PWD: " + password);

            PreparedStatement statement = conn.prepareStatement("SELECT accountID FROM Accounts WHERE email = ? AND pwdhash = ?");
            statement.setString(1,req.getParameter("email"));
            statement.setString(2,pwdhash);


            //statement.setString(3,salt);


            ResultSet resultSet = statement.executeQuery();

            boolean firstRec = true;

						if (!resultSet.next() ) {
							sendFailure(out, "Invalid credentials", null);
						}
            else {
              JSONObject retval = new JSONObject();
              retval.put("accountID", resultSet.getInt("accountID")); 
							sendSuccess(out, retval);
            }
        }
        catch (Exception e)
        {
            //debugging
            sendError(out, "Login failed. Please try again later!", null);
            System.err.println(e);
            e.printStackTrace();
        }

    }

    /*
    //TODO: currently does nothing
    private void doUpdateAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {

            PreparedStatement statement = conn.prepareStatement("UPDATE ");
            int updated = statement.executeUpdate();
            if (updated == 1){
                sendSuccess(out, new JSONObject());
            }
            else{
                sendFailure(out, "Updating Account failed, table not updated", null);
            }
        }
        catch (SQLException e)
        {
            out.println("{\"result\":\"failure\"}");
            System.err.println(e);
            e.printStackTrace();
        }
    }

    //TODO: currently does nothing
    private void doDeleteAccount(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {

            PreparedStatement statement = conn.prepareStatement("DELETE FROM Course WHERE id=?");
            statement.setString(1,req.getParameter("id"));

            int updated = statement.executeUpdate();
            if (updated == 1){
                out.println("{\"result\":\"success\"}");
            }
            else{
                out.println("{\"result\":\"failure\"}");
            }
        }
        catch (SQLException e)
        {
            send
            System.err.println(e);
            e.printStackTrace();
        }
    }*/

    private void doCreateVault(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						String vaultName = req.getParameter("vaultName");
						String vaultDescription = req.getParameter("vaultDescription");

            System.err.println(email);
            System.err.println(password);
						System.err.println(vaultName);
						System.err.println(vaultDescription);
						
						
						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

		            PreparedStatement statement = conn.prepareStatement("INSERT INTO Vaults (accountID, vaultName, vaultDescription) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
		            statement.setInt(1, accountID);
		            statement.setString(2, vaultName);
							  statement.setString(3, vaultDescription);
              int updated = statement.executeUpdate();

              if (updated == 1){
                  //return the vaultID of newly created vault
                  JSONObject retval = new JSONObject();
                  try(ResultSet genKeys = statement.getGeneratedKeys()){
                    if(genKeys.next()){
                      retval.put("vaultID", genKeys.getInt(1));
                    }else{
                      throw new SQLException("Creating vault failed, no ID obtained");
                    }
                    sendSuccess(out, retval);
                  }
              }
              else{
                  sendFailure(out, "Unable to Create Vault for AccountID: " + accountID, null);
              }
					} else {
							sendFailure(out, "Invalid password", null);
					}
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to proccess your request. Try again later!", null);
						e.printStackTrace();
        }

    }

    //returns all vault the user has been GIVEN access to
		private void doGetDelegatedVaults(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						
						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

              System.err.println("doGetDelegatedVaults  with accountID: " + accountID);

		          PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Vaults WHERE vaultId IN (SELECT vaultID FROM Delegates WHERE delegateAccountID = ?)");
		          preparedStatement.setInt(1, accountID);

							ResultSet rs = preparedStatement.executeQuery();

							JSONObject retval = new JSONObject();
							JSONArray vaults = new JSONArray();

							while (rs.next()) {
								
								JSONObject vault = new JSONObject();

								vault.put("vaultID",  rs.getInt("vaultID"));
								vault.put("vaultName",  rs.getString("vaultName"));
								vault.put("vaultDescription",  rs.getString("vaultDescription"));

								vaults.put(vault);		
															
							}

							retval.put("vaults" , vaults);

							sendSuccess(out, retval);

						} else {

							sendFailure(out, "Failed to authenticate.", null);

						}
        }
        catch (Exception e)
        {

 						sendError(out, "The server was unable to process your request. Try again later!",null);

						e.printStackTrace();
        }

    }


		private void doGetVaults(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						
						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

		          PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Vaults WHERE accountID = ?");
		          preparedStatement.setInt(1, accountID);

							ResultSet rs = preparedStatement.executeQuery();

							JSONObject retval = new JSONObject();
							JSONArray vaults = new JSONArray();

							while (rs.next()) {
								
								JSONObject vault = new JSONObject();

								vault.put("vaultID",  rs.getInt("vaultID"));
								vault.put("vaultName",  rs.getString("vaultName"));
								vault.put("vaultDescription",  rs.getString("vaultDescription"));

								vaults.put(vault);		
															
							}

							retval.put("vaults" , vaults);

							sendSuccess(out, retval);

						} else {

							sendFailure(out, "Failed to authenticate.", null);

						}
        }
        catch (Exception e)
        {

 						sendError(out, "The server was unable to process your request. Try again later!",null);

						e.printStackTrace();
        }

    }

    private void doDeleteVault(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						int vaultID = Integer.parseInt(req.getParameter("vaultID"));

						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

              //remove entries associated with vault
              PreparedStatement entriesPreparedStatement = conn.prepareStatement("DELETE FROM Entries WHERE vaultID = ?");
              entriesPreparedStatement.setInt(1, vaultID);
              entriesPreparedStatement.executeUpdate(); //TODO: get value?

              //remove vault itself
		          PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM Vaults WHERE accountID = ? AND vaultID = ?");
		          preparedStatement.setInt(1, accountID);
              preparedStatement.setInt(2, vaultID);

							int updated = preparedStatement.executeUpdate();

              if (updated == 1){
                  System.out.println("Deleting Vault with vaultID: " + vaultID);
                  JSONObject retval = new JSONObject();
                  retval.put("vaultID", vaultID);
                  sendSuccess(out, retval);
              }
              else{
                  sendFailure(out, "Unable to Delete Vault with VaultID: " + vaultID, null);
              }
					  } else {

							sendFailure(out, "Failed to authenticate.", null);
						}
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!",null);

						e.printStackTrace();
        }
    }

    private void doEditVault(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						int vaultID = Integer.parseInt(req.getParameter("vaultID"));
            String vaultName = req.getParameter("vaultName");
            String vaultDescription = req.getParameter("vaultDescription");


						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

              //TODO: give defaults for vaultName and vaultDescription
		          PreparedStatement preparedStatement = conn.prepareStatement("UPDATE Vaults SET vaultName = ?, vaultDescription = ? WHERE accountID = ? AND vaultID = ?", Statement.RETURN_GENERATED_KEYS);
		          preparedStatement.setString(1, vaultName);
              preparedStatement.setString(2, vaultDescription);
              preparedStatement.setInt(3, accountID);
              preparedStatement.setInt(4, vaultID);

							int updated = preparedStatement.executeUpdate();

              if (updated == 1){
                  JSONObject retval = new JSONObject();
                  try(ResultSet genKeys = preparedStatement.getGeneratedKeys()){
                    if(genKeys.next()){
                      retval.put("entryID", genKeys.getInt(1));
                    }else{
                      throw new SQLException("Editting entry failed, no ID obtained");
                    }
                    sendSuccess(out, retval);
                  }
              }
              else{
                  sendFailure(out, "Unable do Update Vault with VaultID: " + vaultID, null);
              }
					  } else {

							sendFailure(out, "Failed to authenticate.", null);
						}
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!",null);

						e.printStackTrace();
        }
    }

    private void doCreateEntry(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						int vaultID = java.lang.Integer.parseInt(req.getParameter("vaultID"));
						String entryName = req.getParameter("entryName");
            String text = req.getParameter("text");

            System.err.println(email);
            System.err.println(password);
						System.err.println(vaultID);
            System.err.println(entryName);
						System.err.println(text);
						
						
						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

		          PreparedStatement statement = conn.prepareStatement("INSERT INTO Entries (vaultID, emailCreatedBy, lastModifiedBy, entryName, text) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
		          statement.setInt(1, vaultID);
              statement.setString(2, email);
              statement.setString(3, email);
		          statement.setString(4, entryName);
							statement.setString(5, text);

              int updated = statement.executeUpdate();

              if (updated == 1){
                  JSONObject retval = new JSONObject();
                  try(ResultSet genKeys = statement.getGeneratedKeys()){
                    if(genKeys.next()){
                      retval.put("entryID", genKeys.getInt(1));
                    }else{
                      throw new SQLException("Creating entry failed, no ID obtained");
                    }
                    sendSuccess(out, retval);
                  }
              }
              else{
                  sendFailure(out, "derp", null);
              }
					  } else {

							  sendFailure(out, "Invalid password", null);

					  }
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!", null);

						e.printStackTrace();
        }

    }

    private void doDeleteEntry(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						int vaultID = java.lang.Integer.parseInt(req.getParameter("vaultID"));
						String entryName = req.getParameter("entryName");

            System.err.println("doDeleteEntry");
            System.err.println(email);
            System.err.println(password);
						System.err.println(vaultID);
            System.err.println(entryName);
						
						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

		          PreparedStatement statement = conn.prepareStatement("DELETE FROM Entries WHERE vaultID = ? AND entryName = ?");
		          statement.setInt(1, vaultID);
		          statement.setString(2, entryName);

              int updated = statement.executeUpdate();

              if (updated == 1){
                  sendSuccess(out, new JSONObject());
              }
              else{
                  sendFailure(out, "Entry with vaultID " + vaultID + " and entryName = " + entryName, null);
              }
					  } else {

							  sendFailure(out, "Invalid password", null);

					  }
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!", null);

						e.printStackTrace();
        }

    }

private void doEditEntry(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						int vaultID = java.lang.Integer.parseInt(req.getParameter("vaultID"));
						String entryName = req.getParameter("entryName");
            String text = req.getParameter("text");

            System.err.println("doEditEntry");
            System.err.println(email);
            System.err.println(password);
						System.err.println(vaultID);
            System.err.println(entryName);
            System.err.println(text);
						
						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

		          PreparedStatement statement = conn.prepareStatement("UPDATE Entries SET text = ?, lastModifiedBy = ? WHERE vaultID = ? AND entryName = ?");
              
              statement.setString(1, text);
              statement.setString(2, email);
		          statement.setInt(3, vaultID);
		          statement.setString(4, entryName);
              
              int updated = statement.executeUpdate();

              if (updated == 1){
                  sendSuccess(out, new JSONObject());
              }
              else{
                  sendFailure(out, "Entry with vaultID " + vaultID + " and entryName = " + entryName, null);
              }

					  } else {

							  sendFailure(out, "Invalid password", null);

					  }
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!", null);

						e.printStackTrace();
        }

    }

    private void doGetEntries(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            int vaultID = java.lang.Integer.parseInt(req.getParameter("vaultID"));

		        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Entries WHERE vaultID = ?");
		        preparedStatement.setInt(1, vaultID);

						ResultSet rs = preparedStatement.executeQuery();

						JSONObject retval = new JSONObject();
						JSONArray entries = new JSONArray();

						while (rs.next()) {
								
							JSONObject entry = new JSONObject();

							entry.put("vaultID",  rs.getInt("vaultID"));
							entry.put("emailCreatedBy",  rs.getString("emailCreatedBy"));
              entry.put("lastModifiedBy", rs.getString("lastModifiedBy"));
							entry.put("entryName",  rs.getString("entryName"));
							entry.put("text",  rs.getString("text"));

							entries.put(entry);		
															
						}

						retval.put("entries" , entries);

						sendSuccess(out, retval);
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!",null);
						e.printStackTrace();
        }

    }

    private void doGetContacts(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email"); //email of owner of contacts list
            String password = req.getParameter("password");

            int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

		          PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Contacts WHERE accountID = ?");
		          preparedStatement.setInt(1, accountID);

              ResultSet rs = preparedStatement.executeQuery();

              JSONObject retval = new JSONObject();
              JSONArray contacts = new JSONArray();

              while (rs.next()) {
								
                JSONObject contact = new JSONObject();

                contact.put("contactEmail",  rs.getString("contactEmail")); //information of contact - acctID
                contact.put("firstName",  rs.getString("firstName"));
                contact.put("lastName", rs.getString("lastName"));
                contact.put("phoneNumber",  rs.getString("phoneNumber"));
                contact.put("address",  rs.getString("address"));
                contact.put("notes",  rs.getString("notes"));

                contacts.put(contact);		
															
              }
              retval.put("contacts" , contacts);
              sendSuccess(out, retval);

					  } else {
							  sendFailure(out, "Invalid password", null);
					  }
        }
        catch (Exception e)
        {
            sendError(out, "The server was unable to process your request. Try again later!",null);
            e.printStackTrace();
        }

    }

    private void doCreateContact(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						String contactEmail = req.getParameter("contactEmail");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phoneNumber = req.getParameter("phoneNumber");
            String address = req.getParameter("address");
            String notes = req.getParameter("notes");
						
						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

              int contactAccountID = getAccountIDFromEmail(contactEmail);

              if(contactAccountID != -1){

		            PreparedStatement statement = conn.prepareStatement("INSERT INTO Contacts (accountID, contactAccountID, contactEmail, firstName, lastName, phoneNumber, address, notes) VALUES(?,?,?,?,?,?,?,?)");
		            statement.setInt(1, accountID);
                statement.setInt(2, contactAccountID);
                statement.setString(3, contactEmail);
		            statement.setString(4, firstName);
							  statement.setString(5, lastName);
							  statement.setString(6, phoneNumber);
							  statement.setString(7, address);
							  statement.setString(8, notes);

                int updated = statement.executeUpdate();

                if (updated == 1){
                    sendSuccess(out, new JSONObject());
                }
                else{
                    sendFailure(out, "Unable to Create Contact", null);
                }
              }else {
                sendFailure(out, "Email does not exist in DB", null);
              }
					  } else {

							  sendFailure(out, "Invalid password", null);
					  }
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!", null);

						e.printStackTrace();
        }

    }

private void doDeleteContact(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						String contactEmail = req.getParameter("contactEmail");

						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

              int contactAccountID = -1;
              contactAccountID = getAccountIDFromEmail(contactEmail);
              if(contactAccountID != -1){

                PreparedStatement contactsPreparedStatement = conn.prepareStatement("DELETE FROM Contacts WHERE accountID = ? AND contactAccountID = ? AND contactEmail = ?");
                contactsPreparedStatement.setInt(1, accountID);
                contactsPreparedStatement.setInt(2, contactAccountID);
                contactsPreparedStatement.setString(3, contactEmail);

							  int updated = contactsPreparedStatement.executeUpdate();

                if (updated == 1){
                    System.out.println("Deleting Contact with email: " + contactEmail);
                    sendSuccess(out, new JSONObject());
                }
                else{
                    sendFailure(out, "Unable to Delete Contact with email: " + contactEmail, null);
                }
              } else {
                  sendFailure(out,"Unable to find ContactAccountID for email: " + contactEmail, null);
              }
					  } else {

							sendFailure(out, "Failed to authenticate.", null);
						}
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!",null);

						e.printStackTrace();
        }
    }

private void doEditContact(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						String contactEmail = req.getParameter("contactEmail");
            String firstName = req.getParameter("firstName");
            String lastName = req.getParameter("lastName");
            String phoneNumber = req.getParameter("phoneNumber");
            String address = req.getParameter("address");
            String notes = req.getParameter("notes");						

						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

              int contactAccountID = -1;
              contactAccountID = getAccountIDFromEmail(contactEmail);
              if(contactAccountID != -1){
		              PreparedStatement statement = conn.prepareStatement("UPDATE Contacts SET firstName = ?, lastName = ?, phoneNumber = ?, address = ?, notes = ? WHERE accountID = ? AND contactAccountID = ? AND contactEmail = ?");
                  statement.setString(1, firstName);
                  statement.setString(2, lastName);
                  statement.setString(3, phoneNumber);
                  statement.setString(4, address);
                  statement.setString(5, notes);
		              statement.setInt(6, accountID);
		              statement.setInt(7, contactAccountID);
                  statement.setString(8, contactEmail);

                  int updated = statement.executeUpdate();

                  if (updated == 1){
                      sendSuccess(out, new JSONObject());
                  }
                  else{
                      sendFailure(out, "Unable to edit contact with email: " + contactEmail, null);
                  }
              } else{
                   sendFailure(out,"Unable to find contactID to edit with email: "+contactEmail,null);
              }
					  } else {

							  sendFailure(out, "Invalid password", null);

					  }
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!", null);

						e.printStackTrace();
        }
    }

private void doEditDelegate(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						int vaultID = java.lang.Integer.parseInt(req.getParameter("vaultID"));
						String delegateEmail = req.getParameter("delegateEmail");
            String privileges = req.getParameter("privileges");

            System.err.println("doEditDelegate");
            System.err.println(email);
            System.err.println(password);
						System.err.println(vaultID);
            System.err.println(delegateEmail);
            System.err.println(privileges);
						
						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

		          PreparedStatement statement = conn.prepareStatement("UPDATE Delegates SET privileges = ?WHERE vaultID = ? AND ownerAccountID = ? AND (delegateAccountId IN (SELECT accountID FROM Accounts WHERE email = ?))");
              
              statement.setString(1, privileges);
              statement.setInt(2, vaultID);
		          statement.setInt(3, accountID);
		          statement.setString(4, delegateEmail);
              
              int updated = statement.executeUpdate();

              if (updated == 1)
                  sendSuccess(out, new JSONObject());
              else
                  sendFailure(out, "Unable to edit delegate", null);

					  } else {

							  sendFailure(out, "Invalid password", null);

					  }
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!", null);

						e.printStackTrace();
        }

    }

    private void doDeleteDelegate(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						int vaultID = java.lang.Integer.parseInt(req.getParameter("vaultID"));
						String delegateEmail = req.getParameter("delegateEmail");

            System.err.println("doDeleteDelegate");
            System.err.println(email);
            System.err.println(password);
						System.err.println(vaultID);
            System.err.println(delegateEmail);
						
						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

		          PreparedStatement statement = conn.prepareStatement("DELETE FROM Delegates WHERE vaultID = ? AND ownerAccountID = ? AND delegateAccountId IN (SELECT accountID FROM Accounts WHERE email = ?)");
		          statement.setInt(1, vaultID);
		          statement.setInt(2, accountID);
              statement.setString(3, delegateEmail);

              int updated = statement.executeUpdate();

              if (updated == 1){
                  sendSuccess(out, new JSONObject());
              }
              else{
                  sendFailure(out, "Unable to delete delegate", null);
              }
					  } else {
							  sendFailure(out, "Invalid password", null);
					  }
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!", null);

						e.printStackTrace();
        }
    }

    private void doCreateDelegate(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
						int vaultID = java.lang.Integer.parseInt(req.getParameter("vaultID"));
						String delegateEmail = req.getParameter("delegateEmail");
            String privileges = req.getParameter("privileges");
						
						int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

              int delegateAccountID = -1;
              delegateAccountID = getAccountIDFromEmail(delegateEmail);
              if(delegateAccountID != -1){

		            PreparedStatement statement = conn.prepareStatement("INSERT INTO Delegates (vaultID, ownerAccountID, delegateEmail, privileges, delegateAccountID) VALUES(?,?,?,?,?)");
		            statement.setInt(1, vaultID);
                statement.setInt(2, accountID);
                statement.setString(3, delegateEmail);
		            statement.setString(4, privileges);
                statement.setInt(5, delegateAccountID);


                int updated = statement.executeUpdate();

                if (updated == 1){
                    sendSuccess(out, new JSONObject());
                }
                else{
                    sendFailure(out, "Unable to add delegate", null);
                }
              }else {
                  sendFailure(out,"Unable to add delegate - No account with email " + delegateEmail + " exists in DB", null);
              }
					  } else {
							  sendFailure(out, "Invalid password", null);
					  }
        }
        catch (Exception e)
        {
 						sendError(out, "The server was unable to process your request. Try again later!", null);

						e.printStackTrace();
        }

    }

private void doGetDelegates(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {    
        PrintWriter out = resp.getWriter();
        try
        {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            int vaultID = Integer.parseInt(req.getParameter("vaultID"));

            int accountID = -1; 
						if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {

		          PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Delegates WHERE ownerAccountID = ? AND vaultID = ?");
		          preparedStatement.setInt(1, accountID);
              preparedStatement.setInt(2, vaultID);

              ResultSet rs = preparedStatement.executeQuery();

              JSONObject retval = new JSONObject();
              JSONArray delegates = new JSONArray();

              while (rs.next()) {
								
                JSONObject delegate = new JSONObject();

                delegate.put("delegateEmail",  rs.getString("delegateEmail"));
                delegate.put("privileges",  rs.getString("privileges"));

                delegates.put(delegate);		
															
              }
              retval.put("delegates", delegates);
              sendSuccess(out, retval);

					  } else {
							  sendFailure(out, "Invalid password", null);
					  }
        }
        catch (Exception e)
        {
            sendError(out, "The server was unable to process your request. Try again later!",null);
            e.printStackTrace();
        }
    }

private void doShareVault(HttpServletRequest req, HttpServletResponse resp) throws IOException
{
    PrintWriter out = resp.getWriter();
    try{
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        int vaultID = Integer.parseInt(req.getParameter("vaultID"));
        String delegateEmail = req.getParameter("delegateEmail");
        long keyMost = Long.parseLong(req.getParameter("keyMost"));
        long keyLeast = Long.parseLong(req.getParameter("keyLeast"));

        int accountID = -1;
				if((accountID = authenticateAndReturnAccountID(email, password)) != -1) {
            int delegateAccountID = -1;
            if((delegateAccountID = getAccountIDFromEmail(delegateEmail)) != -1) {
                PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO Keys (accountID, vaultID, keyMost, keyLeast) VALUES (?,?,?,?)");
                preparedStatement.setInt(1, delegateAccountID);
                preparedStatement.setInt(2, vaultID);
                preparedStatement.setLong(3, keyMost);
                preparedStatement.setLong(4, keyLeast);

                int updated = preparedStatement.executeUpdate();
                if(updated == 1){
                    sendSuccess(out, new JSONObject());
                } else{
                    sendFailure(out, "Unable to share vault", null);
                }
            }else {
                sendFailure(out,"Delegate Email "+ delegateEmail +" does not exist in DB", null);
            }
        }else {
            sendFailure(out, "Invalid password", null);
        }
    }
    catch (Exception e){
        sendError(out, "The server was unable to process your request. Try again later!",null);
        e.printStackTrace();
    }
}

private void doCreateKey(HttpServletRequest req, HttpServletResponse resp) throws IOException
{
    PrintWriter out = resp.getWriter();
    try{
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        int vaultID = Integer.parseInt(req.getParameter("vaultID"));
        long keyMost = Long.parseLong(req.getParameter("keyMost"));
        long keyLeast = Long.parseLong(req.getParameter("keyLeast"));
        String cipherData = req.getParameter("cipherData");
        byte[] challenge = (req.getParameter("challenge")).getBytes(); //Does this work?
        String responseData = req.getParameter("responseData");

        int accountID = -1;
        if((accountID = authenticateAndReturnAccountID(email,password)) != -1){
            conn.setAutoCommit(false);
            try{
                PreparedStatement insertKeyStatement = conn.prepareStatement("INSERT INTO VaultKeys(UUID_Most_Significant, UUID_Least_Significant, cipherData, challenge, responseData) VALUES(?,?,?,?,?)");
                insertKeyStatement.setLong(1, keyMost);
                insertKeyStatement.setLong(2, keyLeast);
                insertKeyStatement.setString(3, cipherData);
                insertKeyStatement.setBytes(4, challenge);
                insertKeyStatement.setString(5, responseData);
                insertKeyStatement.executeUpdate();

                //statement to insert to AccountToVaultToKey table
                PreparedStatement insertAVKStatement = conn.prepareStatement("INSERT INTO AccountToVaultToKey (accountID, vaultID, keyMost, keyLeast) VALUES(?,?,?,?)");
                insertAVKStatement.setInt(1, accountID);
                insertAVKStatement.setInt(2, vaultID);
                insertAVKStatement.setLong(3, keyMost);
                insertAVKStatement.setLong(4, keyLeast);
                insertAVKStatement.executeUpdate();

                conn.commit();
                conn.setAutoCommit(true); //TODO: is this neccessary?

                sendSuccess(out, new JSONObject());
            }catch(SQLException sqle){
                sendFailure(out, "An SQL Exception has occurred.", null);
                sqle.printStackTrace();
            }
            
        }else {
            sendFailure(out, "Invalid password", null);
        }
    }catch(Exception e){
        sendError(out, "The server was unable to process your request. Try again later!",null);
        e.printStackTrace();
    }
}

    public byte[] getHash(String password) throws Exception {
      MessageDigest digest = MessageDigest.getInstance("SHA-512");
      digest.reset();
      byte[] input = digest.digest(password.getBytes("UTF-8"));
      return input;
    }

		//TODO: Make second parameter a JSON object and append its parse to data
		private void sendSuccess(PrintWriter stream, JSONObject data) {

				System.out.println(data);
				stream.println("{");
				stream.println("\tresult : \"success\",\n");
				stream.println("\tdata : " + data);
				stream.println("}");

		}

		private void sendFailure(PrintWriter stream, String errorMessage, JSONObject data) {

				System.out.println(errorMessage);
				stream.println("{");
				stream.println("\tresult : \"failure\",\n");
				stream.println("\tmessage : \"" + errorMessage + "\",");
        stream.println("\tdata : " + data);
				stream.println("}");
		}

		private void sendError(PrintWriter stream, String errorMessage,JSONObject data) {

				System.out.println(errorMessage);
				stream.println("{");
				stream.println("\tresult : \"error\",\n");
				stream.println("\tmessage : \"" + errorMessage + "\"");
				stream.println("\tdata : \"" + data + "\"");
				stream.println("}");

		}

    public int getAccountIDFromEmail(String email) throws IOException{
        try{
          //get accountID using email
          PreparedStatement getContactIDStmt = conn.prepareStatement("SELECT accountID FROM Accounts WHERE email = ?");
          getContactIDStmt.setString(1, email);
          ResultSet rs = getContactIDStmt.executeQuery();
          int accountID = -1;
          while(rs.next()){
            accountID = rs.getInt("accountID");
          }
          return accountID;
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        return -1;
    }
    




		public int authenticateAndReturnAccountID (String email, String password) throws Exception{

            String salt = "";

            PreparedStatement saltStatement = conn.prepareStatement("SELECT salt FROM Accounts WHERE email = ?");

            saltStatement.setString(1,email);
            ResultSet saltResultSet = saltStatement.executeQuery();

            if(saltResultSet.first()){

                salt = saltResultSet.getString("salt");

            } else{

                return -1;
            }

            String pwdhash = new String(getHash(password + salt));

            PreparedStatement statement = conn.prepareStatement("SELECT accountID FROM Accounts WHERE email = ? AND pwdhash = ?");
            statement.setString(1,email);
            statement.setString(2,new String(getHash(password + salt)));

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()){
              return -1;
            }

					return resultSet.getInt("accountID");

		}

		public boolean isLockedOut (int accountID){return false;}



    public String nextSessionId() {
      return new BigInteger(130, new SecureRandom()).toString(32);
    }
    

}
