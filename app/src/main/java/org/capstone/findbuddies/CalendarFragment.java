package org.capstone.findbuddies;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_OK;

public class CalendarFragment extends Fragment {
    ArrayList<CalendarMemoItem> Memos = new ArrayList<>();
    private static final int GROUP_MEMO_NO = 3000;
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
    FloatingActionButton but;
    int Year=0;
    int Month=0;
    int Date=0;
    int GroupNo;
    int Backend = 0;
    int BackendMonth = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar_view,container,false);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        myEmail = getArguments().getString("myEmail");
        getMyID(myID);
        MCalendarView = rootView.findViewById(R.id.MCalendarView);
        but = rootView.findViewById(R.id.AddCalendarMemoBut);

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Month==0){
                    Toast.makeText(getContext(), "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("myEmail", myEmail);
                    GroupNo = 0;
                    bundle.putInt("GroupNo", GroupNo);
                    bundle.putInt("CalendarAdd",1);
                    bundle.putInt("Year",Year);
                    bundle.putInt("Month",Month);
                    bundle.putInt("Date",Date);
                    MemoEditFragment memoEditFragment = new MemoEditFragment();
                    memoEditFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_main, memoEditFragment)
                            .commit();
                    ((NavigationMain) getActivity()).isEdit = 2;
                }
            }
        });
        but.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(Month==0){
                    Toast.makeText(getContext(), "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getContext(),AddGroupMemo.class);
                    intent.putExtra("MyEmail",myEmail);
                    startActivityForResult(intent,GROUP_MEMO_NO);
                }
                return false;
            }
        });
        listview = rootView.findViewById(R.id.SpecificDateMemoList);
        isGroup = 0;
        getMemoDate();
        MCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Year = date.getYear();
                Month = date.getMonth()+1;
                Date = date.getDay();

                String shot_Day = Year + "," + Month + "," + Date;
                ShowSelectedDateMemo(Year,Month,Date);
                Backend=1;
            }
        });

        MCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                ShowMonthMemo(date.getMonth()+1);
                Backend=0;
                BackendMonth=date.getMonth()+1;
            }
        });

        MCalendarView.addDecorators(
                new SaturdayDecorator(),
                new SundayDecorator(),
                new todayDecorator()
        );




        adapter = new SpecificDateMemoAdapter();
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReadMemo(position);
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("정말로 삭제하시겠습니까?");
                builder.setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                database.getReference().child("MemoList").child(Memos.get(position).getUidKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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


    private void ReadMemo(int position) {
        CalendarMemoItem memo = Memos.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("myEmail",myEmail);
        bundle.putInt("GroupNo",memo.getGroupNo());
        bundle.putInt("READ",1);
        bundle.putString("ReadTitle",memo.getTitle());
        bundle.putString("ReadContent",memo.getContent());
        bundle.putString("UidKey",memo.getUidKey());
        if(memo.getPictureURI()!=null){
            bundle.putString("ReadPictureURI",memo.getPictureURI());
        }
        if(memo.getLatitude()!=0){
            bundle.putDouble("ReadLatitude",memo.getLatitude());
            bundle.putDouble("ReadLongitude",memo.getLongitude());
        }
        bundle.putInt("ReadYear",memo.getYear());
        bundle.putInt("ReadMonth",memo.getMonth());
        bundle.putInt("ReadDay",memo.getDate());
        bundle.putInt("ReadHour",memo.getHour());
        bundle.putInt("ReadMinute",memo.getMinute());
        bundle.putString("ReadUploader",memo.getUploader());
        MemoEditFragment memoEditFragment = new MemoEditFragment();
        memoEditFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_main,memoEditFragment)
                .commit();
        ((NavigationMain)getActivity()).isEdit =2;
    }

    public void ShowSelectedDateMemo(int year, int month, int dayOfMonth){

        database.getReference().child("MemoList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Memos.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SaveMemo value = snapshot.getValue(SaveMemo.class);
                    if (value != null) {
                        if (value.getUploaderEmail().equals(myEmail) || CheckMyEmailInGroup(value.getCheckGroupNo())) {
                            CalendarMemoItem memoItem = null;

                            if(year == value.getYear()&& month == value.getMonth()&& dayOfMonth == value.getDate()){
                                memoItem = new CalendarMemoItem(value.getCheckGroupNo(),value.getUploaderEmail(),snapshot.getKey(),value.getYear(),value.getMonth(),value.getDate(),
                                        value.getHour(),value.getMinute(),value.getTitle(),value.getMemo(),value.getImageUrl(),value.getLatitude(),value.getLongitude(),value.getAddress());
                                Memos.add(memoItem);
                            }


                        }
                    }
                }
                Collections.sort(Memos,sortByHourAndMinute);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void ShowMonthMemo(int month){
        database.getReference().child("MemoList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Memos.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SaveMemo value = snapshot.getValue(SaveMemo.class);
                    if (value != null) {
                        if (value.getUploaderEmail().equals(myEmail) || CheckMyEmailInGroup(value.getCheckGroupNo())) {
                            CalendarMemoItem memoItem = null;

                            if(month == value.getMonth()){
                                memoItem = new CalendarMemoItem(value.getCheckGroupNo(),value.getUploaderEmail(),snapshot.getKey(),value.getYear(),value.getMonth(),value.getDate(),
                                        value.getHour(),value.getMinute(),value.getTitle(),value.getMemo(),value.getImageUrl(),value.getLatitude(),value.getLongitude(),value.getAddress());
                                Memos.add(memoItem);
                            }
                        }
                    }
                }
                Collections.sort(Memos,sortByDate);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }



    class SpecificDateMemoAdapter extends BaseAdapter {
        SpecificDateMemoAdapter(){
            BackendMonth = CalendarDay.today().getMonth()+1;
            database.getReference().child("MemoList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Memos.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SaveMemo value = snapshot.getValue(SaveMemo.class);
                        if (value != null) {
                            if (value.getUploaderEmail().equals(myEmail) || CheckMyEmailInGroup(value.getCheckGroupNo())) {
                                if(value.getMonth()==CalendarDay.today().getMonth()+1){
                                    CalendarMemoItem memoItem = null;
                                    memoItem = new CalendarMemoItem(value.getCheckGroupNo(),value.getUploaderEmail(),snapshot.getKey(),value.getYear(),value.getMonth(),value.getDate(),
                                            value.getHour(),value.getMinute(),value.getTitle(),value.getMemo(),value.getImageUrl(),value.getLatitude(),value.getLongitude(),value.getAddress());
                                    Memos.add(memoItem);
                                }
                            }
                        }
                    }
                    Collections.sort(Memos,sortByDate);
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
                view = getLayoutInflater().inflate(R.layout.calendar_memo_item,null);
                viewHolder = new ViewHolder();
                viewHolder.CalendarDate = view.findViewById(R.id.calendar_date);
                viewHolder.MapIcon = view.findViewById(R.id.map_icon);
                viewHolder.GroupIcon = view.findViewById(R.id.group_icon);
                viewHolder.TitleOrContent = view.findViewById(R.id.title_or_content);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)view.getTag();
            }
            CalendarMemoItem Memo = Memos.get(position);
            String ShowDate = Memo.getDate()+"";
            String ShowTime = Memo.getHour() +":"+Memo.getMinute();
            if(ShowDate.length()==1){
                ShowDate = "0"+ ShowDate;
            }
            if(Backend==1){
                viewHolder.CalendarDate.setVisibility(View.VISIBLE);
                viewHolder.CalendarDate.setText(ShowTime);
            }
            else if(Backend==0){
                viewHolder.CalendarDate.setVisibility(View.VISIBLE);
                viewHolder.CalendarDate.setText(ShowDate);
            }
            if(Memo.getLatitude()!=0){
                viewHolder.MapIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.MapIcon.setVisibility(View.GONE);
            }
            if(Memo.getGroupNo()!=0) {
                viewHolder.GroupIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.GroupIcon.setVisibility(View.GONE);
            }
            if(Memo.getTitle()==null||Memo.getTitle().trim().length()==0){
                viewHolder.TitleOrContent.setText(Memo.getContent());
            }
            else{
                viewHolder.TitleOrContent.setText(Memo.getTitle());
            }
            return view;
        }
        class ViewHolder{
            TextView CalendarDate;
            ImageView MapIcon;
            TextView GroupIcon;
            TextView TitleOrContent;
        }
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
                    int nation = nowAddress.indexOf("대한민국");
                    if(nation!=-1){
                        nowAddress = nowAddress.substring(nation+5);
                    }
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

    private final static Comparator<CalendarMemoItem> sortByHourAndMinute = new Comparator<CalendarMemoItem>() {
        @Override
        public int compare(CalendarMemoItem o1, CalendarMemoItem o2) {
            int ret = 0;
            if(o1.getHour()>o2.getHour()){
                ret = 1;
            }
            else if(o1.getHour()==o2.getHour()){
                if(o1.getMinute()>o2.getMinute()){
                    ret = 1;
                }
                else if(o1.getMinute()==o2.getMinute()){
                    ret = 0;
                }
                else {
                    ret = -1;
                }
            }
            else {
                ret = -1;
            }
            return ret;
        }
    };
    private final static Comparator<CalendarMemoItem> sortByDate = new Comparator<CalendarMemoItem>() {
        @Override
        public int compare(CalendarMemoItem o1, CalendarMemoItem o2) {
            if(o1.getDate()>o2.getDate()){
                Log.d("ParsingTest","o1크다. 리턴값 : "+Integer.compare(o1.getDate(),o2.getDate()));
            }
            else if(o1.getDate()<o2.getDate()){
                Log.d("ParsingTest","o1작다. 리턴값 : "+Integer.compare(o1.getDate(),o2.getDate()));
            }

            return Integer.compare(o1.getDate(),o2.getDate());
        }
    };

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

            MCalendarView.addDecorator(new EventDecorator(Color.parseColor("#263687"), calendarDays,getActivity()));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GROUP_MEMO_NO){
            if(resultCode==RESULT_OK){
                GroupNo = data.getIntExtra("GroupNo",0);
                MemoEditFragment memoEditFragment = new MemoEditFragment();
                Bundle bundle = new Bundle();
                bundle.putString("myEmail",myEmail);
                Log.d("ParsingTest","no: "+GroupNo);
                bundle.putInt("GroupNo",GroupNo);
                bundle.putInt("CalendarAdd",1);
                bundle.putInt("Year",Year);
                bundle.putInt("Month",Month);
                bundle.putInt("Date",Date);
                memoEditFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_main,memoEditFragment)
                        .commit();
                ((NavigationMain)getActivity()).isEdit =2;
            }
        }
    }
}
