package com.myapplication3.parichay.ads;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.myapplication3.parichay.MainActivity;
import com.myapplication3.parichay.R;
import com.myapplication3.parichay.subscription.JsonParse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmClientCertificate;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class pay_ad extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    String customerId = "";
    String orderId = "";
    String mid = "rZDPnL05323351660175";
    String amount = "";
    public String docid = "";
    private  HashMap<String, String> paramMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);

        customerId = generateString();
        orderId = generateString();
        amount = getIntent().getStringExtra("amount");
        docid = getIntent().getStringExtra("documentid");


        getCheckSum cs = new getCheckSum();
        cs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public class getCheckSum extends AsyncTask<ArrayList<String>, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(pay_ad.this);

        String url ="https://sskshatriya.000webhostapp.com/paytm/generateChecksum.php";
        //TODO your server's url here (www.xyz/checksumGenerate.php)
        String varifyurl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + orderId;
                /*"https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";*/
        String CHECKSUMHASH ="";

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JsonParse jsonParser = new JsonParse(pay_ad.this);
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+customerId+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+amount+"&WEBSITE=DEFAULT"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";

            Log.e("PostData",param);

            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {

                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            PaytmPGService Service = PaytmPGService.getProductionService();
            paramMap = new HashMap<>();
            paramMap.put("MID", "rZDPnL05323351660175");
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", customerId);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", amount);
            paramMap.put("WEBSITE", "DEFAULT");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());

            PaytmClientCertificate Certificate = new PaytmClientCertificate("client123", "clientcert");
            Service.initialize(Order,Certificate);
            Service.startPaymentTransaction(pay_ad.this, true,
                    true,
                    pay_ad.this  );
        }

    }

    @Override
    public void onTransactionResponse(Bundle bundle) {


        if (bundle.getString("STATUS").equals("TXN_SUCCESS")) {
            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


            FirebaseFirestore.getInstance().collection("advertises").document(uid).collection("myads")
                    .document(docid).update("status","").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {


                    FirebaseFirestore.getInstance().collection("advertises").document(uid).collection("myads")
                            .document(docid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                            String img = task.getResult().getString("adimg");
                            String tit = task.getResult().getString("adtitle");
                            String exp = task.getResult().getString("expiry");

                            new_ads add = new new_ads(tit,img,exp);

                            FirebaseFirestore.getInstance().collection("ads").add(add).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Toast.makeText(pay_ad.this, "Advertise Successfully Published", Toast.LENGTH_SHORT).show();
                                    FirebaseFirestore.getInstance().collection("payments").document(uid).collection(orderId).add(paramMap);
                                    finish();
                                }
                            });
                        }
                    });

                }
            });

        }

        finish();
       /*
         Bundle[{STATUS=TXN_SUCCESS, CHECKSUMHASH=uFwM18wuGA85AZGpkC8X5tzT/NSvpKFL13Sv2lvGW6PZBri2PR4VPrUvV+ISbLJwWEeO2aLoqi1bYN4zvGjptJxSgHMmFkaepl8dey5OM8c=,
         ORDERID=15820b0e00de4d76b8597a6f88f836d4,
         TXNAMOUNT=1.00,
         TXNDATE=2019-07-11 20:32:33.0,
         MID=aVZOfL98465894658946,
         TXNID=20190711111212800110168227578295333,
         RESPCODE=01,
         PAYMENTMODE=UPI,
         BANKTXNID=919244872866,
         CURRENCY=INR,
         GATEWAYNAME=PPBLC,
         RESPMSG=Txn Success}]
         */

    }

    private void gotosuccess() {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
    }

    private void gotofail() {
        Intent in = new Intent(this, pay_ad.class);
        startActivity(in);
    }

    @Override
    public void networkNotAvailable() {
        Log.e("Trans ", "Network Not Available" );
        finish();
    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Log.e("Trans ", " Authentication Failed  "+ s );
        finish();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("Trans ", " ui fail respon  "+ s );
        finish();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("Trans ", " error loading page responce true "+ s + "  s1 " + s1);
        finish();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(this, "Transaction Cancel", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Toast.makeText(this, "Transaction Cancel", Toast.LENGTH_LONG).show();
        Log.e("Trans ", "  transaction cancel " );

    }

    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}