package MetroSystem.src.metroSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

public class Database {

    private volatile static Database uniqueInstance;
    private ArrayList<Station> allStations;
    private ArrayList<Edge> allEdges;
    private ArrayList<Line> allLines;
    private float[][] priceHK, priceSZ;
    private int stationsHK, stationsSZ, edgesHK, edgesSZ;

    private Database() {
        allStations = new ArrayList<>();
        allEdges = new ArrayList<>();
        allLines = new ArrayList<>();
    }

    /**
     * Apply double-checked locking method to create this important singleton object.
     * @return  the unique Database instance
     * @since   Sept. 29, 2021
     */
    public static Database getInstance() {
        if(uniqueInstance == null) {
            synchronized (Database.class) {
                if(uniqueInstance == null) {
                    uniqueInstance = new Database();
                }
            }
        }
        return uniqueInstance;
    }

    public void loadStations() {
        XSSFWorkbook workbookHK = null, workbookSZ = null;
        try {
            System.out.println("Current workspace: " + System.getProperty("user.dir"));
            File fileHK = new File("./MetroSystem/data/stations_HK.xlsx");
            File fileSZ = new File("./MetroSystem/data/stations_SZ.xlsx");
            InputStream inputStreamHK = new FileInputStream(fileHK);
            InputStream inputStreamSZ = new FileInputStream(fileSZ);
            workbookHK = new XSSFWorkbook(inputStreamHK);
            workbookSZ = new XSSFWorkbook(inputStreamSZ);
        }
        catch (Exception e) {
            System.out.println("Fail to load file!\nDetails: " + e);
        }
        XSSFSheet sheetHK = workbookHK.getSheetAt(0);
        XSSFSheet sheetSZ = workbookSZ.getSheetAt(0);
        for (Row row : sheetHK) {
            if (row.getRowNum() == 0)
                continue;
            String englishName = row.getCell(0).getStringCellValue();
            String traditionalChineseName = row.getCell(1).getStringCellValue();
            String simplifiedChineseName = row.getCell(2).getStringCellValue();
            allStations.add(new Station(allStations.size() + 1, englishName, traditionalChineseName, simplifiedChineseName, AdministratorHK.getInstance()));
        }
        stationsHK = allStations.size();
        for (Row row : sheetSZ) {
            if (row.getRowNum() == 0)
                continue;
            String englishName = row.getCell(0).getStringCellValue();
            String traditionalChineseName = row.getCell(1).getStringCellValue();
            String simplifiedChineseName = row.getCell(2).getStringCellValue();
            allStations.add(new Station(allStations.size() + 1, englishName, traditionalChineseName, simplifiedChineseName, AdministratorSZ.getInstance()));
        }
        stationsSZ = allStations.size() - stationsHK;
    }

    public void loadEdges() {
        XSSFWorkbook workbookHK = null, workbookSZ = null, workbookBorder = null;
        try {
            File fileHK = new File("./MetroSystem/data/edges_HK.xlsx");
            File fileSZ = new File("./MetroSystem/data/edges_SZ.xlsx");
            File fileBorder = new File("./MetroSystem/data/edges_border.xlsx");
            InputStream inputStreamHK = new FileInputStream(fileHK);
            InputStream inputStreamSZ = new FileInputStream(fileSZ);
            InputStream inputStreamBorder = new FileInputStream(fileBorder);
            workbookHK = new XSSFWorkbook(inputStreamHK);
            workbookSZ = new XSSFWorkbook(inputStreamSZ);
            workbookBorder = new XSSFWorkbook(inputStreamBorder);
        }
        catch (Exception e) {
            System.out.println("Fail to load file!\nDetails: " + e);
        }
        XSSFSheet sheetHK = workbookHK.getSheetAt(0);
        XSSFSheet sheetSZ = workbookSZ.getSheetAt(0);
        XSSFSheet sheetBorder = workbookBorder.getSheetAt(0);
        for (Row row : sheetHK) {
            if (row.getRowNum() == 0)
                continue;
            Station st_station = allStations.get((int) row.getCell(0).getNumericCellValue() - 1);
            Station ed_station = allStations.get((int) row.getCell(1).getNumericCellValue() - 1);
            int time = (int) row.getCell(2).getNumericCellValue();
            allEdges.add(new Edge(allEdges.size() + 1, st_station, ed_station, time, AdministratorHK.getInstance()));
            allEdges.add(new Edge(allEdges.size() + 1, ed_station, st_station, time, AdministratorHK.getInstance()));
        }
        edgesHK = allEdges.size() / 2;
        for (Row row : sheetSZ) {
            if (row.getRowNum() == 0)
                continue;
            Station st_station = allStations.get(stationsHK + (int)row.getCell(0).getNumericCellValue() - 1);
            Station ed_station = allStations.get(stationsHK + (int)row.getCell(1).getNumericCellValue() - 1);
            int time = (int) row.getCell(2).getNumericCellValue();
            allEdges.add(new Edge(allEdges.size() + 1, st_station, ed_station, time, AdministratorSZ.getInstance()));
            allEdges.add(new Edge(allEdges.size() + 1, ed_station, st_station, time, AdministratorSZ.getInstance()));
        }
        edgesSZ = allEdges.size() / 2 - edgesHK;
        for (Row row : sheetBorder) {
            if (row.getRowNum() == 0)
                continue;
            Station st_station = allStations.get((int)row.getCell(0).getNumericCellValue() - 1);
            Station ed_station = allStations.get(stationsHK + (int)row.getCell(1).getNumericCellValue() - 1);
            boolean isOpen = ((int)row.getCell(2).getNumericCellValue()) == 0? false : true;
            int time = (int) row.getCell(3).getNumericCellValue();
            Edge tempEdge = new Edge(allEdges.size() + 1, st_station, ed_station, time, AdministratorBorder.getInstance());
            allEdges.add(tempEdge);
            tempEdge.setIsOpen(isOpen);
            tempEdge = new Edge(allEdges.size() + 1, ed_station, st_station, time, AdministratorBorder.getInstance());
            allEdges.add(tempEdge);
            tempEdge.setIsOpen(isOpen);
        }
    }

    public void loadLines() {
        XSSFWorkbook workbookHK = null, workbookSZ = null;
        try {
            File fileHK = new File("./MetroSystem/data/lines_HK.xlsx");
            File fileSZ = new File("./MetroSystem/data/lines_SZ.xlsx");
            InputStream inputStreamHK = new FileInputStream(fileHK);
            InputStream inputStreamSZ = new FileInputStream(fileSZ);
            workbookHK = new XSSFWorkbook(inputStreamHK);
            workbookSZ = new XSSFWorkbook(inputStreamSZ);
        }
        catch (Exception e) {
            System.out.println("Fail to load file!\nDetails: " + e);
        }
        XSSFSheet sheetHK = workbookHK.getSheetAt(0);
        XSSFSheet sheetSZ = workbookSZ.getSheetAt(0);



        for (Row row : sheetHK) {
            if (row.getRowNum() == 0)
                continue;
            ArrayList<Edge> tempEdges = new ArrayList<>();
            String englishName = row.getCell(0).getStringCellValue();
            String traditionalChineseName = row.getCell(1).getStringCellValue();
            String simplifiedChineseName = row.getCell(2).getStringCellValue();
            int l = (int)row.getCell(3).getNumericCellValue(), r = (int)row.getCell(4).getNumericCellValue();
            for (int i = (l - 2) * 2; i <= (r - 2) * 2 + 1; i++)
                tempEdges.add(allEdges.get(i));
            allLines.add(new Line(allLines.size() + 1, englishName, traditionalChineseName, simplifiedChineseName, AdministratorHK.getInstance(), tempEdges));
        }
        for (Row row : sheetSZ) {
            if (row.getRowNum() == 0)
                continue;
            ArrayList<Edge> tempEdges = new ArrayList<>();
            String englishName = row.getCell(0).getStringCellValue();
            String traditionalChineseName = row.getCell(1).getStringCellValue();
            String simplifiedChineseName = row.getCell(2).getStringCellValue();
            int l = (int)row.getCell(3).getNumericCellValue() + edgesHK, r = (int)row.getCell(4).getNumericCellValue() + edgesHK;
            for (int i = (l - 2) * 2; i <= (r - 2) * 2 + 1; i++)
                tempEdges.add(allEdges.get(i));
            allLines.add(new Line(allLines.size() + 1, englishName, traditionalChineseName, simplifiedChineseName, AdministratorSZ.getInstance(), tempEdges));
        }
    }

    public void loadPrice() {
        XSSFWorkbook workbookHK = null, workbookSZ = null;
        try {
            File fileHK = new File("./MetroSystem/data/price_HK.xlsx");
            File fileSZ = new File("./MetroSystem/data/price_SZ.xlsx");
            InputStream inputStreamHK = new FileInputStream(fileHK);
            InputStream inputStreamSZ = new FileInputStream(fileSZ);
            workbookHK = new XSSFWorkbook(inputStreamHK);
            workbookSZ = new XSSFWorkbook(inputStreamSZ);
        }
        catch (Exception e) {
            System.out.println("Fail to load file!\nDetails: " + e);
        }
        XSSFSheet sheetHK = workbookHK.getSheetAt(0);
        XSSFSheet sheetSZ = workbookSZ.getSheetAt(0);

        Row row = sheetHK.getRow(sheetHK.getLastRowNum());
        int rows = sheetHK.getLastRowNum(), columns = row.getLastCellNum() - 1;

        priceHK = new float[stationsHK + 1][stationsHK + 1];
        for(int i = 1; i <= rows; i++) {
            row = sheetHK.getRow(i);
            for (int j = 1; j <= columns; j++) {
                float price = (float) row.getCell(j).getNumericCellValue();
                int startStationId = (int)sheetHK.getRow(i).getCell(0).getNumericCellValue();
                int endStationId = (int)sheetHK.getRow(0).getCell(j).getNumericCellValue();
                priceHK[startStationId][endStationId] = price;
            }
        }

        row = sheetSZ.getRow(sheetSZ.getLastRowNum());
        rows = sheetSZ.getLastRowNum();
        columns = row.getLastCellNum() - 1;

        priceSZ = new float[stationsSZ + 1][stationsSZ + 1];
        for(int i = 1; i <= rows; i++) {
            row = sheetSZ.getRow(i);
            for (int j = 1; j <= columns; j++) {
                float price = (float) row.getCell(j).getNumericCellValue();
                int startStationId = (int)sheetSZ.getRow(i).getCell(0).getNumericCellValue();
                int endStationId = (int)sheetSZ.getRow(0).getCell(j).getNumericCellValue();
                priceSZ[startStationId][endStationId] = price;
            }
        }
    }

    public Line getLineByName(String name, Language language) {
        for(Line l : allLines) {
            if(l.getNameInSpecificLanguage(language).equals(name))
                return l;
        }
        return null;
    }

    public Station getStationByName(String name, Language language, Administrator admin) {
        for(Station s : allStations) {
            if(s.getNameInSpecificLanguage(language).equals(name) && s.getAdmin() == admin)
                return s;
        }
        return null;
    }

    /***
     * Find a station according to its station_id.
     * @param id    The id of your target station
     * @return      The reference of your target station
     * @throws ExStationNotFound    If the given id cannot be matching with any station in database
     */
    public Station getStationById(int id) throws ExStationNotFound {
        for(Station s : allStations) {
            if(s.getId() == id) {
                return s;
            }
        }
        String exp = "Station of id = " + id +" cannot be found in our database!";
        throw new ExStationNotFound(exp);
    }

    public int getStationCount(){
        return allStations.size();
    }

    public ArrayList<Edge> getEdges(){
        return allEdges;
    }

    /***
     * Translate an array of station id to its station name
     * @param ids       An arraylist of station_id in integer format
     * @return An arraylist of station_name in your desired language with the original order
     */
    public ArrayList<String> translateId2Name(ArrayList<Integer> ids){
        ArrayList<String> names = new ArrayList<>();
        for (int id : ids) {
            String name = "?";
            try{
                name = getStationById(id).getName();
            }
            catch (ExStationNotFound e){
                System.out.println(e.getMessage());
            }
            finally {
                names.add(name);
            }
        }
        return names;
    }

    public void getPrice(Station startStation, Station endStation) {
        if(startStation.getAdmin() == endStation.getAdmin()) {
            int startStationId = startStation.getId(), endStationId = endStation.getId();
            if(startStation.getAdmin() == AdministratorHK.getInstance())
                System.out.println(startStation.getName() + "->" + endStation.getName() + ": " + priceHK[startStationId][endStationId]);
            else if(startStation.getAdmin() == AdministratorSZ.getInstance())
                System.out.println(startStation.getName() + "->" + endStation.getName() + ": " + priceSZ[startStationId - stationsHK][endStationId - stationsHK]);
        }
        else {
            if(MetroSystem.getInstance().getSystemLanguage() == Language.English)
                System.out.println("The two stations belong to different administrations, and the cross-segment calculation should be carried out according to the stations through the route");
            if(MetroSystem.getInstance().getSystemLanguage() == Language.TraditionalChinese)
                System.out.println("兩站屬於不同管轄範圍，需根據路線經過站進行跨段計算");
            if(MetroSystem.getInstance().getSystemLanguage() == Language.SimplifiedChinese)
                System.out.println("两站属于不同管辖范围，需根据路线经过站进行跨段计算");
        }
    }
}