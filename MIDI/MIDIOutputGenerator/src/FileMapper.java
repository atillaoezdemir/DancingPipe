import java.io.File;
import java.io.Serializable;

public class FileMapper implements Serializable {
    double lowerBorder;
    double upperBorder;
    File file;

    public FileMapper(File file, double lowerBorder, double upperBorder) {
        this.file = file;
        this.lowerBorder = lowerBorder;
        this.upperBorder = upperBorder;
    }
    public void exportAsJson() {

    }

}
