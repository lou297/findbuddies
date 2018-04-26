package org.capstone.findbuddies;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsingText {
    static public class NameEntity {
        final String text;
        final String type;
        Integer count;
        public NameEntity (String text, String type, Integer count) {
            this.text = text;
            this.type = type;
            this.count = count;
        }
    }
    public class DownloadJson extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try{
                return ParsingText(strings[0]);
            }catch (Exception e){
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            parsingContent = s;
        }

        private String ParsingText(String Text){
            String openApiURL = "http://aiopen.etri.re.kr:8000/WiseNLU";
            String accessKey = "b80a3310-cd3a-4bf6-804b-39973c11c27f"; // 발급받은 Access Key
            String analysisCode = "ner"; // 언어 분석 코드
            String text = Text; // 분석할 텍스트 데이터
            Gson gson = new Gson();

            Map<String, Object> request = new HashMap<>();
            Map<String, String> argument = new HashMap<>();

            argument.put("analysis_code", analysisCode);
            argument.put("text", text);

            request.put("access_key", accessKey);
            request.put("argument", argument);


            URL url;
            Integer responseCode = null;
            String responBodyJson = null;
            String responBody = "";
            Map<String, Object> responeBody = null;
            try {
                url = new URL(openApiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.write(gson.toJson(request).getBytes("UTF-8"));
                wr.flush();
                wr.close();

                responseCode = con.getResponseCode();
                InputStream is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer();

                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                responBodyJson = sb.toString();

                // http 요청 오류 시 처리
                if ( responseCode != 200 ) {
                    // 오류 내용 출력
                    Log.d("ParsingTest","responseCode != 200 오류 발생");
                    return "오류 발생";
                }

                responeBody = gson.fromJson(responBodyJson, Map.class);
                Integer result = ((Double) responeBody.get("result")).intValue();
                Map<String, Object> returnObject;
                List<Map> sentences;

                // 분석 요청 오류 시 처리
                if ( result != 0 ) {

                    // 오류 내용 출력
                    Log.d("ParsingTest","result != 0 오류 발생");
                    return "오류 발생";
                }

                returnObject = (Map<String, Object>) responeBody.get("return_object");
                sentences = (List<Map>) returnObject.get("sentence");

                Map<String, NameEntity> nameEntitiesMap = new HashMap<String, NameEntity>();
                List<NameEntity> nameEntities = null;
                Log.d("ParsingTest","123");
                for( Map<String, Object> sentence : sentences ) {
                    List<Map<String, Object>> nameEntityRecognitionResult = (List<Map<String, Object>>) sentence.get("NE");
                    for( Map<String, Object> nameEntityInfo : nameEntityRecognitionResult ) {
                        String name = (String) nameEntityInfo.get("text");
                        NameEntity nameEntity = nameEntitiesMap.get(name);
                        if ( nameEntity == null ) {
                            nameEntity = new NameEntity(name, (String) nameEntityInfo.get("type"), 1);
                            nameEntitiesMap.put(name, nameEntity);
                            Log.d("ParsingTest","6");
                        } else {
                            nameEntity.count = nameEntity.count + 1;
                        }
                    }
                }
                if ( 0 < nameEntitiesMap.size() ) {
                    nameEntities = new ArrayList<NameEntity>(nameEntitiesMap.values());
                }
                for( NameEntity nameEntity: nameEntities){
                    responBody += nameEntity.text +"("+ nameEntity.type+")\n";
                }
                Log.d("ParsingTest",responBody);
            } catch (MalformedURLException e) {
                Log.d("ParsingTest","1"+e.getMessage());
            } catch (IOException e) {
                Log.d("ParsingTest","2"+e.getMessage());
            }
            return responBody;
        }
    }
}
