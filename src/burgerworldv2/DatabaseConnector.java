/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package burgerworldv2;

import static com.oracle.webservices.internal.api.EnvelopeStyle.Style.XML;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import jdk.internal.org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mattp
 */
public class DatabaseConnector {

    static String URL, user, password;
    static Connection connection;

    public DatabaseConnector() {
        this.URL = "jdbc:mysql://localhost:3306/bw_product_database";
        this.user = "root";
        this.password = "1";
        getConnection();
    }

    private void getConnection() {
        try {
            connection = DriverManager.getConnection(URL, user, password);
            //System.out.println("Connection Successful!");
        } catch (SQLException ex) {
            //System.out.println("Error getting connection: " + ex.getMessage());
        } catch (Exception ex) {
            //System.out.println("Error: " + ex.getMessage());
        }
    }

    private static String getTagValue(String stringTag, Element e) {
        NodeList nodeList = e.getElementsByTagName(stringTag).item(0).getChildNodes();
        Node nodeValue = (Node) nodeList.item(0);
        return nodeValue.getNodeValue();
    }

    private static Product getProduct(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            int id = Integer.parseInt(getTagValue("id", element));
            String name = getTagValue("name", element);
            double price = Double.parseDouble(getTagValue("price", element));
            int curNum = Integer.parseInt(getTagValue("amountOfProduct", element));
            int fullNum = Integer.parseInt(getTagValue("fullNum", element));
            return new Product(id, name, price, curNum, fullNum);
        }
        return new Product();
    }

    private static Account getAccount(Node node) {
        //XMLReaderDOM domReader = new XMLReaderDOM();\
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            int accNum = Integer.parseInt(getTagValue("accountNumber", element));
            String password = getTagValue("password", element);
            double balance = Double.parseDouble(getTagValue("accountBalance", element));
            String cusName = getTagValue("customerName", element);
            String cusAddress = getTagValue("customerAddress", element);
            String cusPhone = getTagValue("customerPhoneNum", element);
            return new Account(accNum, password, balance, cusName, cusAddress, cusPhone);
        }
        return new Account();
    }

    public void bootstrap(ArrayList<Account> accountArray, ArrayList<Product> productArray) {
        try {
            //XML to ArrayList parameters
            File productXML = new File("C:\\Users\\mattp\\Desktop\\BurgerWorldv2\\src\\burgerworldv2\\productInfo.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(productXML);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Product");
            for (int i = 0; i < nodeList.getLength(); i++) {
                productArray.add(getProduct(nodeList.item(i)));
            }
//            for (Product p : productArray) {
//                System.out.println(p.toString());
//            }

            File accountXML = new File("C:\\Users\\mattp\\Desktop\\BurgerWorldv2\\src\\burgerworldv2\\accountInfo.xml");
            doc = dBuilder.parse(accountXML);
            doc.getDocumentElement().normalize();
            nodeList = doc.getElementsByTagName("Account");
            for (int i = 0; i < nodeList.getLength(); i++) {
                accountArray.add(getAccount(nodeList.item(i)));
            }
            for (Account a : accountArray) {
                double balance = a.getAccountBalance();
                int id = a.getAccountNum();
                updateAccount(balance, id);
            }
//            for(Account a: accountArray) {
//                System.out.println(a.toString());
//            }
//          System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            refreshDatabase(productArray);
        } catch (Exception ex) {
            ex.getStackTrace();
            System.out.println(ex);
        }
    }

    public static void updateAccount(double newBalance, int idNum) {
        String query = "UPDATE account_tbl \n"
                + "SET \n"
                + "    balanceAccount = " + newBalance + "\n"
                + "WHERE\n"
                + "    numAccount = " + idNum + ";";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(query);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Exception while executing statement: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("General exception while executing query: " + ex.getMessage());
        }
        try {
            String filepath = "C:\\Users\\mattp\\Desktop\\BurgerWorldv2\\src\\burgerworldv2\\accountInfo.xml";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);

            int index = -1;
            String s = "" + idNum;
            if (doc.getElementsByTagName("accountNumber").item(0).getTextContent().equals(s)) {
                index = 0;
            } else if (doc.getElementsByTagName("accountNumber").item(1).getTextContent().equals(s)) {
                index = 1;
            } else if (doc.getElementsByTagName("accountNumber").item(2).getTextContent().equals(s)) {
                index = 2;
            }

            Node accountBalanceNode = doc.getElementsByTagName("accountBalance").item(index);
            NodeList list = accountBalanceNode.getChildNodes();
            accountBalanceNode.setTextContent("" + newBalance);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }//Netbeans didn't like this one :(
        //        catch (SAXException sae) {
        //            sae.printStackTrace();
        //        } 
        catch (org.xml.sax.SAXException ex) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void updateAmount(int idNum, int amount) {
        String query = "UPDATE product_tbl \n"
                + "SET \n"
                + "    amountOfProduct = " + amount + "\n"
                + "WHERE\n"
                + "    idProduct = " + idNum + ";";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(query);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Exception while executing statement: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("General exception while executing query: " + ex.getMessage());
        }
        try {
            String filepath = "C:\\Users\\mattp\\Desktop\\BurgerWorldv2\\src\\burgerworldv2\\productInfo.xml";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);

            int index = -1;
            String s = "" + idNum;
            if (doc.getElementsByTagName("id").item(0).getTextContent().equals(s)) {
                index = 0;
            } else if (doc.getElementsByTagName("id").item(1).getTextContent().equals(s)) {
                index = 1;
            } else if (doc.getElementsByTagName("id").item(2).getTextContent().equals(s)) {
                index = 2;
            } else if (doc.getElementsByTagName("id").item(3).getTextContent().equals(s)) {
                index = 3;
            } else if (doc.getElementsByTagName("id").item(4).getTextContent().equals(s)) {
                index = 4;
            } else if (doc.getElementsByTagName("id").item(5).getTextContent().equals(s)) {
                index = 5;
            } else if (doc.getElementsByTagName("id").item(6).getTextContent().equals(s)) {
                index = 6;
            }

            Node accountBalanceNode = doc.getElementsByTagName("amountOfProduct").item(index);
            NodeList list = accountBalanceNode.getChildNodes();
            accountBalanceNode.setTextContent("" + amount);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }//Netbeans didn't like this one :(
        //        catch (SAXException sae) {
        //            sae.printStackTrace();
        //        } 
        catch (org.xml.sax.SAXException ex) {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void refreshDatabase(ArrayList<Product> productArray) {
        for (Product p : productArray) {
            int newAmount = p.getAmountOfProduct();
            int id = p.getId();
            updateAmount(id, newAmount);
        }
    }

}
