package testandormi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Class used to manage files inside the sdcard, such actions include for example to save the file inside the device.
 * @author airton
 *
 */
public class ManageFile {
    private static final String TAG = "ManageFile";
    private boolean sdCardAvailable;
    private boolean sdCardWritableReadable;
    private boolean sdCardReadableOnly;
	private String logName;
    
    public ManageFile(String logName){ 
        this.logName = logName;
    }

    /**
     * Escreve no arquivo texto.
     * @param text Texto a ser escrito.
     * @return True se o texto foi escrito com sucesso.
     * @throws IOException 
     */
	public boolean WriteFile(String text) throws IOException{
    	File file = new File(logName);
    	
    	if (!file.exists()) {
			file.createNewFile();
		}
        try {
            FileOutputStream out = new FileOutputStream(file, true);
            out.write("\n".getBytes());
            out.write(text.getBytes());
            out.flush();
            out.close();    
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean clearFile(){
    	File file = new File("Log.txt");
    	try {
			RandomAccessFile r = new RandomAccessFile(file, "rws");
			r.setLength(0);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
    }
    
    /**
     * Faz a leitura do arquivo
     * @return O texto lido.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String ReadFile()	 throws FileNotFoundException, IOException{
        File textfile = new File("romar.txt");

        FileInputStream input = new FileInputStream(textfile);
        byte[] buffer = new byte[(int)textfile.length()];
        
        input.read(buffer);            
        
        return new String(buffer);
    }
    

    public boolean isSdCardAvailable() {
        return sdCardAvailable;
    }

    public void setSdCardAvailable(boolean sdCardAvailable) {
        this.sdCardAvailable = sdCardAvailable;
    }

    public boolean isSdCardWritableReadable() {
        return sdCardWritableReadable;
    }

    public void setSdCardWritableReadable(boolean sdCardWritableReadable) {
        this.sdCardWritableReadable = sdCardWritableReadable;
    }

    public boolean isSdCardReadableOnly() {
        return sdCardReadableOnly;
    }

    public void setSdCardReadableOnly(boolean sdCardReadableOnly) {
        this.sdCardReadableOnly = sdCardReadableOnly;
    }

}