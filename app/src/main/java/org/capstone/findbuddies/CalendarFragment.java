package org.capstone.findbuddies;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CalendarFragment extends Fragment {
    ArrayList<MemoItem> Memos = new ArrayList<>();
    CalendarView calendarView;
    MaterialCalendarView MCalendarView;
    ArrayList<String> DateMemoList;
    String myEmail;
    String myID;
    boolean Check;
    ListView listview;
    SpecificDateMemoAdapter adapter;
    FirebaseDatabase database;
    FirebaseStorage storage;
    int isGroup;
    private List<String> uidLists = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar_view,container,false);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        myEmail = getArguments().getString("myEmail");
        getMyID(myID);
        calendarView = rootView.findViewById(R.id.calendarView);
        MCalendarView = rootView.findViewById(R.id.MCalendarView);
        listview = rootView.findViewById(R.id.SpecificDateMemoList);
        isGroup = 0;
        getMemoDate();
        MCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth()+1;
                int Date = date.getDay();

                String shot_Day = Year + "," + Month + "," + Date;


//                MCalendarView.clearSelection();
//                adapter = new SpecificDateMemoAdapter(Year,Month,Date);
//                new SpecificDateMemoAdapter(Year,Month,Date);
                ShowSelectedDateMemo(Year,Month,Date);
//                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), shot_Day , Toast.LENGTH_SHORT).show();

            }
        });

        MCalendarView.addDecorators(
                new SaturdayDecorator(),
                new SundayDecorator(),
                new todayDecorator()
        );


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                SpecificDateMemoAdapter adapter = new SpecificDateMemoAdapter(year,month,dayOfMonth);
                ShowSelectedDateMemo(year,month,dayOfMonth);
//                listview.setAdapter(adapter);
            }
        });

        adapter = new SpecificDateMemoAdapter(0,0,0);
        listview.setAdapter(adapter);

//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getContext(),MemoEdit.class);
//
//                startActivity(intent);
//            }
//        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("정말로 삭제하시겠습니까?");
                builder.setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                database.getReference().child("MemoList").child(uidLists.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Memos.remove(position);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which){
                            }
                        });
                builder.show();
                return false;
            }
        });
        return rootView;
    }

    public void ShowSelectedDateMemo(int year, int month, int dayOfMonth){

        database.getReference().child("MemoList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uidLists.clear();
                Memos.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SaveMemo value = snapshot.getValue(SaveMemo.class);
                    String uidKey = snapshot.getKey();
                    if (value != null) {
                        if (value.getUploaderEmail().equals(myEmail) || CheckMyEmailInGroup(value.getCheckGroupNo())) {
                            MemoItem memoItem = null;
                            String address = null;
                            if(value.getLatitude()!=0){
                                address = getLocationAddress(value.getLatitude(),value.getLongitude());
                            }

                            if(year == value.getYear()&& month == value.getMonth()&& dayOfMonth == value.getDate()){
                                String date_label = value.getMonth()+"월 "+value.getDate()+"일";
                                if(value.getLatitude()==0){
                                    memoItem = new MemoItem(value.getEditSystemTime(),date_label,value.getYear(),value.getMonth(),value.getDate(),value.getCheckGroupNo(),
                                            value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),null);
                                }
                                else {
                                    memoItem = new MemoItem(value.getEditSystemTime(),date_label,value.getYear(),value.getMonth(),value.getDate(),value.getCheckGroupNo(),
                                            value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),address);
                                }
                            }
                            if(memoItem!=null){
                                uidLists.add(uidKey);
                                addMemo(memoItem);
                            }

                        }
                    }
                }
                Log.d("ParsingTest","SIZE2: "+Memos.size());
                adapter.notifyDataSetChanged();
//                listview.invalidateViews();
//                listview.refreshDrawableState();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }



    class SpecificDateMemoAdapter extends BaseAdapter {

        private SpecificDateMemoAdapter(final int year, final int month, final int dayOfMonth) {
            database.getReference().child("MemoList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    uidLists.clear();
                    Memos.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        SaveMemo value = snapshot.getValue(SaveMemo.class);
                        String uidKey = snapshot.getKey();
                        if(value !=null){
                            if(value.getUploaderEmail().equals(myEmail) || CheckMyEmailInGroup(value.getCheckGroupNo())){
                                MemoItem memoItem = null;
                                String address = null;
                                if(year ==0){
                                    if(value.getLatitude()!=0){
                                        address = getLocationAddress(value.getLatitude(),value.getLongitude());
                                    }

                                    if(value.getMonth()==0){
                                        if(value.getLatitude()==0){
                                            memoItem = new MemoItem(value.getEditSystemTime(),null,0,0,0,value.getCheckGroupNo(),
                                                    value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),null);
                                        }
                                        else{
                                            memoItem = new MemoItem(value.getEditSystemTime(),null,0,0,0,value.getCheckGroupNo(),
                                                    value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),address);
                                        }

                                    }
                                    else{
                                        String date_label = value.getMonth()+"월 "+value.getDate()+"일";
                                        if(value.getLatitude()==0){
                                            memoItem = new MemoItem(value.getEditSystemTime(),date_label,value.getYear(),value.getMonth(),value.getDate(),value.getCheckGroupNo(),
                                                    value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),null);
                                        }
                                        else {
                                            memoItem = new MemoItem(value.getEditSystemTime(),date_label,value.getYear(),value.getMonth(),value.getDate(),value.getCheckGroupNo(),
                                                    value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),address);
                                        }
                                    }
                                    if(memoItem!=null){
                                        uidLists.add(uidKey);
                                        addMemo(memoItem);
                                    }

                                }
                                else {
                                    if(value.getLatitude()!=0){
                                        address = getLocationAddress(value.getLatitude(),value.getLongitude());
                                    }

                                    if(year == value.getYear()&& month == value.getMonth()&& dayOfMonth == value.getDate()){
                                        String date_label = value.getMonth()+"월 "+value.getDate()+"일";
                                        if(value.getLatitude()==0){
                                            memoItem = new MemoItem(value.getEditSystemTime(),date_label,value.getYear(),value.getMonth(),value.getDate(),value.getCheckGroupNo(),
                                                    value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),null);
                                        }
                                        else {
                                            memoItem = new MemoItem(value.getEditSystemTime(),date_label,value.getYear(),value.getMonth(),value.getDate(),value.getCheckGroupNo(),
                                                    value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),address);
                                        }
                                    }
                                    uidLists.add(uidKey);
                                    addMemo(memoItem);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



        @Override
        public int getCount() {
            return Memos.size();
        }

        @Override
        public Object getItem(int position) {
            return Memos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;
            if(view==null){
                view = getLayoutInflater().inflate(R.layout.memo_item,null);
                viewHolder = new ViewHolder();
                viewHolder.date_label = view.findViewById(R.id.date_label);
                viewHolder.group_label = view.findViewById(R.id.group_label);
                viewHolder.title = view.findViewById(R.id.title);
                viewHolder.date = view.findViewById(R.id.date);
                viewHolder.content = view.findViewById(R.id.contents);
                viewHolder.picture = view.findViewById(R.id.picture);
                viewHolder.location = view.findViewById(R.id.memo_location);

                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)view.getTag();
            }

            MemoItem Memo = Memos.get(position);
            if(Memo!=null){
                if(Memo.getDate_label()==null) {
                    viewHolder.date_label.setVisibility(View.GONE);
                }
                else{
                    viewHolder.date_label.setVisibility(View.VISIBLE);
                    viewHolder.date_label.setText(Memo.getDate_label());
                }
                if(Memo.getGroup_label()==0){
                    viewHolder.group_label.setVisibility(View.GONE);
                }
                else {
                    viewHolder.group_label.setVisibility(View.VISIBLE);
                }

//            if(Memo.getDate_label()==null&&Memo.getGroup_label()==0){
//                Resources r = getResources();
//                float pxLeftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
//                float pxTopMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
//                float pxRightMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
//                float pxBottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
//
//                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                params.setMargins(Math.round(pxLeftMargin), Math.round(pxTopMargin), Math.round(pxRightMargin), Math.round(pxBottomMargin));
//                viewHolder.content_layout.setLayoutParams(params);
//            }

                viewHolder.title.setText(Memo.getTitle());
                viewHolder.date.setText(Memo.getDate());
                viewHolder.content.setText(Memo.getContents());
                if(Memo.getPictureURI()!=null){
                    viewHolder.picture.setVisibility(View.VISIBLE);
                    StorageReference storageReference = storage.getReferenceFromUrl(Memo.getPictureURI());
                    Glide.with(getContext())
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(viewHolder.picture);
                }
                else{
                    viewHolder.picture.setVisibility(View.GONE);
                }
//
                if(Memo.getLocation()==null){
                    viewHolder.location.setVisibility(View.GONE);
                }
                else{
                    viewHolder.location.setVisibility(View.VISIBLE);
                    viewHolder.location.setText(Memo.getLocation());
                }
            }

            return view;
        }
        class ViewHolder{
            TextView date_label;
            TextView group_label;
            TextView title;
            TextView date;
            TextView content;
            ImageView picture;
            TextView location;
//            LinearLayout content_layout;
        }

    }
    public void addMemo(MemoItem memoItem){
        Memos.add(memoItem);
    }
    public String getLocationAddress(double latitude,double longitude){
        String nowAddress ="현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(getContext(), Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
                address = geocoder.getFromLocation(latitude, longitude, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    nowAddress  = address.get(0).getAddressLine(0);

                }
            }

        } catch (IOException e) {
            Toast.makeText(getContext(), "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
        return nowAddress;
    }

    private void getMemoDate(){
        DateMemoList = new ArrayList<>();
        database.getReference().child("MemoList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SaveMemo value = snapshot.getValue(SaveMemo.class);
                    if (value != null) {
                        if (value.getUploaderEmail().equals(myEmail)) {
                            //////Log.d("ParsingTest",value.getMemo()+", "+value.getMonth());
                            if(value.getMonth()!=0){
                                String DecoDate = value.getYear()+","+value.getMonth()+","+value.getDate();
                                DateMemoList.add(DecoDate);
                            }
                        }
                    }
                }
                new ApiSimulator(DateMemoList).executeOnExecutor(Executors.newSingleThreadExecutor());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getMyID(String MyEmail) {
        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if (value != null && (value.getSavedEmail()).equals(MyEmail)) {
                        myID = value.getSavedID();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private boolean CheckMyEmailInGroup(int GroupNo){
        Check = false;
        database.getReference().child("GruopList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveGroupList value = snapshot.getValue(SaveGroupList.class);
                    if(value!=null){
                        if(value.getGroupNo()==GroupNo){
                            ArrayList<String> membersID = value.getMembersID();
                            for(String memberCheck : membersID){
                                if(memberCheck.equals(myID)){
                                    Check = true;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return Check;
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        ArrayList<String> Time_Result;

        ApiSimulator(ArrayList<String> Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for (int i = 0; i < Time_Result.size(); i++) {
                CalendarDay day = CalendarDay.from(calendar);

                String[] time = Time_Result.get(i).split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int date = Integer.parseInt(time[2]);
                CalendarDay newday = CalendarDay.from(year,month-1,date);
                dates.add(newday);
            }


            return dates;
        }


                @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);


            if (getActivity().isFinishing()) {
                return;
            }

            MCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays,getActivity()));
        }
    }

    private class EventDecorator implements DayViewDecorator {

        private final Drawable drawable;
        private int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates, Activity context) {
            drawable = context.getResources().getDrawable(R.drawable.calendar_dot);
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
//            view.setSelectionDrawable(drawable);
            view.addSpan(new DotSpan(5, color)); // 날자밑에 점
        }
    }

    private class SundayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();

        public SundayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }
    private class SaturdayDecorator implements DayViewDecorator {

        private final Calendar calendar = Calendar.getInstance();
        public SaturdayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SATURDAY;
        }
        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }
    private class todayDecorator implements DayViewDecorator{
        private CalendarDay date;
        private todayDecorator(){
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
//            view.addSpan(new StyleSpan(Typeface.BOLD));
//            view.addSpan(new RelativeSizeSpan(1.4f));
            view.addSpan(new ForegroundColorSpan(Color.GREEN));
        }

        public void setDate(Date date){
            this.date = CalendarDay.from(date);
        }
    }
}
