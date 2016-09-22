package workload;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO documentation
 */
public class TraceWorkLoad {

    private List<Sample> samples = new ArrayList<Sample>();

    public void addSample(Sample sample) {
        samples.add(sample);
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void build(String fileName, int cNo) {
        try {
            FileInputStream file = new FileInputStream(new File(fileName));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            List<String> tenantNames = new ArrayList<String>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                int i = 0;
                int k = 0;
                Sample sample = null;
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        k++;
                        String value = cell.getStringCellValue();
                        if (k > 1) {
                            tenantNames.add(value.trim());
                        }
                    } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        i++;
                        double value = cell.getNumericCellValue();
                        if (i == 1) {
                            sample = new Sample((int) value);
                            samples.add(sample);
                        } else {
                            assert sample != null;
                            int j = i - 2;
                            sample.addTenantUnit(
                                    new TenantUnit(tenantNames.get(j), (int) value));
                        }
                    }
                }
            }
            file.close();

//            File fout = new File("resources/out2.txt");
//            FileOutputStream fos = new FileOutputStream(fout);
//
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
//
//            for (Sample sampleV : samples) {
//                bw.write(String.valueOf(sampleV.getSampleId()));
//                bw.write("\t");
//                for (TenantUnit unit : sampleV.getTenantUnits()) {
//                    bw.write(String.valueOf(unit.getVSNId()));
//                    bw.write("\t");
//                    bw.write(String.valueOf(unit.getUsers()));
//                    bw.write("\t");
//                }
//                bw.newLine();
//            }
//            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
