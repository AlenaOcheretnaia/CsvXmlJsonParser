import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;

import java.io.*;
import java.util.*;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.csv";
        List<Employee> listCsvEmployee = parseCSV(columnMapping, csvFileName);
        assert listCsvEmployee != null;
        if (!listCsvEmployee.isEmpty())
            listToJson(listCsvEmployee, "dataCsv.json");
        else
            System.out.println("CSV файл пуст или не был прочитан корректно!");

        String xmlFileName = "data.xml";
        List<Employee> listXmlEmployee = parseXML(xmlFileName);
        if (!listXmlEmployee.isEmpty())
            listToJson(listXmlEmployee, "dataXml.json");
        else
            System.out.println("XML файл пуст или не был прочитан корректно!");
    }

    private static List<Employee> parseXML(String filename) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employeeList = new LinkedList<>();
        //Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse( new File(filename));

        Node root = doc.getDocumentElement();
        System.out.println("Корневой элемент: " + root.getNodeName());

        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element employee = (Element) node;
                NodeList nodeEmp = employee.getChildNodes();
                ArrayList<String> argsEmployee = new ArrayList<>();
                for (int j = 0; j < nodeEmp.getLength(); j++) {
                    Node node1 = nodeEmp.item(j);
                    if (Node.ELEMENT_NODE == node1.getNodeType()) {
                        String attrName = node1.getNodeName();
                        String attrValue = node1.getTextContent();
                        //System.out.println("Атрибут: " + attrName + "; значение: " + attrValue);
                        argsEmployee.add(attrValue);
                    }
                }
                System.out.println(argsEmployee);
                //получаем параметры для объекта
                if (argsEmployee.size() == 5) {
                    long paramId = Long.parseLong(argsEmployee.get(0));
                    String paramFirstName = argsEmployee.get(1);
                    String paramLastName = argsEmployee.get(2);
                    String paramCountry = argsEmployee.get(3);
                    int paramAge = Integer.parseInt(argsEmployee.get(4));
                    employeeList.add(new Employee(paramId, paramFirstName, paramLastName, paramCountry,paramAge));
                }
            }
        }
        return employeeList;
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return (csv.parse());
        } catch (IOException e) {
            e.printStackTrace();
            return(null);
        }
    }

    public static void listToJson(List<Employee> listEmployee, String filename) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String jsonS = gson.toJson(listEmployee, listType);
        writeString(jsonS, filename);
    }

    public static void writeString(String jsonS, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(jsonS);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace(); }
    }

}
