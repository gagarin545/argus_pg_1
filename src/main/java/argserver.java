import java.io.IOException;
import java.util.concurrent.*;

public class argserver {

    public static void main(String[] args) {

        StoreInquiry storeInquiry = new StoreInquiry() ;

        try {
            storeInquiry.inquiryOrder();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
