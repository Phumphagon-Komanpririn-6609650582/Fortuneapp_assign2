package com.example.sensors;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorManager;
    private static final int POLL_INTERVAL = 500;
    private Handler hdr = new Handler();
    private PowerManager.WakeLock wl;
    SensorInfo sensor_info = new SensorInfo();
    Boolean shown_dialog = false;
    private static final int shake_threshold = 15;

    private final Random randomstick = new Random();
    private static final int STICK_MIN = 1;
    private static final int STICK_MAX = 10;

    private int drawStick() {
        return randomstick.nextInt(STICK_MAX - STICK_MIN + 1) + STICK_MIN;
    }

    private String fortuneFor(int n) {
        switch (n) {
            case 1:  return "\"ดีเยี่ยมเปี่ยมสุข\"\n" +
                    "ดวงชะตานี้อำนวยอวยชัย ทำสิ่งใดล้วนสำเร็จสมมาดปรารถนาเหมือนมีบุญเก่าหนุนนำ การงานก้าวหน้ามั่นคง การเงินไหลมาเทมาดุจสายน้ำ ความรักสดใสสมหวังดั่งใจปอง สุขภาพแข็งแรงดีไร้โรคาพยาธิ";
            case 2:  return "\"โชคลาภวาสนา\"\n" +
                    "เป็นช่วงเวลาแห่งความรุ่งเรือง โอกาสดีกำลังเข้ามาใกล้ สิ่งที่มุ่งหวังไว้จะบรรลุผลสำเร็จโดยง่ายดาย ปัญหาอุปสรรคที่มีจะคลี่คลายไปเองตามกาลเวลา ค้าขายได้กำไรมาก มีเกณฑ์ได้ลาภลอย";
            case 3:  return "\"ปานกลางพึงเพียร\"\n" +
                    "เรื่องที่ถามมีความเป็นไปได้ที่จะสำเร็จ แต่ต้องใช้ความพยายามและอดทนอย่างต่อเนื่อง อย่ารีบร้อนหรือใจร้อน ทุกอย่างต้องใช้เวลาค่อยเป็นค่อยไป แล้วจะสมหวังในที่สุด";
            case 4:  return "\"เสมอตัวพอเพียง\"\n" +
                    "ช่วงนี้ชีวิตค่อนข้างราบเรียบ ไม่มีเรื่องร้ายแรงให้กังวล แต่ก็ไม่มีโชคใหญ่เข้ามา ควรดำเนินชีวิตอย่างมีสติและไม่ประมาท ใช้ชีวิตตามปกติสุข ดูแลตนเองและคนรอบข้างให้ดีssssssssss";
            case 5:  return "\"ระวังภัยพาล\"\n" +
                    "มีเกณฑ์พบเจอปัญหาหรืออุปสรรคเล็กน้อยถึงปานกลาง ควรใช้ความรอบคอบในการตัดสินใจเรื่องสำคัญ ไม่ควรวางใจผู้อื่นมากเกินไป หากมีการเดินทางต้องระวังเป็นพิเศษ";
            case 6:  return "\"อดทนรอคอย\"\n" +
                    "เปรียบเสมือนเรือที่รอคอยลมที่ท่าเรือ ต้องใช้ความอดทนเป็นอย่างมากในการรอจังหวะและโอกาสที่ดี สิ่งที่ดูไม่ชัดเจนในตอนนี้จะค่อยๆ คลี่คลายเมื่อเวลาผ่านไป ไม่ควรลงทุนที่มีความเสี่ยงสูง";
            case 7:  return "\"ผิดหวังคลาดเคลื่อน\"\n" +
                    "สิ่งที่คาดหวังอาจไม่เป็นไปตามที่คิด มีเกณฑ์ที่จะผิดหวังหรือคลาดเคลื่อนจากเป้าหมายที่วางไว้ ควรทำใจให้เข้มแข็งและเตรียมพร้อมรับมือกับสถานการณ์ที่ไม่คาดคิด";
            case 8:  return "\"สุขภาพน่าห่วง\"\n" +
                    "คำทำนายเตือนให้ระมัดระวังปัญหาสุขภาพ ทั้งของตนเองและคนในครอบครัว หากมีอาการเจ็บป่วยควรรีบไปพบแพทย์ทันที และดูแลเอาใจใส่เรื่องอาหารการกินเป็นพิเศษ";
            case 9:  return "\"มิตรภาพไม่แน่นอน\"\n" +
                    "ความสัมพันธ์กับคนรอบข้างหรือเพื่อนฝูงอาจมีปัญหา ไม่แน่นอนอย่างที่คิด อาจมีการขัดแย้งหรือเข้าใจผิดกันเกิดขึ้น ควรใช้ความนิ่งและมีสติในการสื่อสารเพื่อป้องกันปัญหา";
            case 10: return "\"ใบล่างสุดคลุมเครือ\"\n" +
                    "ช่วงเวลานี้เปรียบเสมือนการฝันที่คลุมเครือ ทุกอย่างดูเต็มไปด้วยความไม่แน่นอนและความลำบากในการตัดสินใจหรือสื่อสาร สิ่งที่ตั้งใจอาจไม่เป็นไปตามหวัง ควรตั้งสติและรอจังหวะที่ดีกว่าในอนาคต";
            default: return "ดวงกลางๆ ทำอะไรมีสติ ผลจะดีเอง";
        }
    }


    private final Runnable pollTask = new Runnable() {
        public void run() {
            showDialog();
            hdr.postDelayed(pollTask, POLL_INTERVAL);
        }
    };

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
/*
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null){
            TextView text1 = findViewById(R.id.textView1);
            text1.setText(R.string.accelerometer_na);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) == null){
            TextView text2 = findViewById(R.id.textView2);
            text2.setText(R.string.gravity_na);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null){
            TextView text3 = findViewById(R.id.textView3);
            text3.setText(R.string.gyroscope_na);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) == null){
            TextView text4 = findViewById(R.id.textView4);
            text4.setText(R.string.light_na);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) == null){
            TextView text5 = findViewById(R.id.textView5);
            text5.setText(R.string.linear_acceleration_na);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null){
            TextView text6 = findViewById(R.id.textView6);
            text6.setText(R.string.magnetic_field_na);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) == null){
            TextView text7 = findViewById(R.id.textView7);
            text7.setText(R.string.orientation_na);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null){
            TextView text8 = findViewById(R.id.textView8);
            text8.setText(R.string.proximity_na);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null){
            TextView text9 = findViewById(R.id.textView9);
            text9.setText(R.string.rotation_vector_na);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) == null){
            TextView text10 = findViewById(R.id.textView10);
            text10.setText(R.string.ambient_temp_na);
        }

 */
        TextView textView = findViewById(R.id.textView);


        textView.setText("เขย่ามือถือเพื่อรับคำทำนาย...");


        textView.setAlpha(0f);
        textView.setTranslationY(80f);


        textView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(1500)
                .setStartDelay(300);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Sensors Info");
    }//end onCreate

    public void onAccuracyChanged(Sensor sensor, int accuracy){
        // TO DO
    }//end onAccuracyChanged

    public void onSensorChanged(SensorEvent event){
        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            sensor_info.accX=event.values[0];
            sensor_info.accY=event.values[1];
            sensor_info.accZ=event.values[2];
        }
        if (type == Sensor.TYPE_GRAVITY) {
            sensor_info.graX=event.values[0];
            sensor_info.graY=event.values[1];
            sensor_info.graZ=event.values[2];
        }
        if (type == Sensor.TYPE_GYROSCOPE) {
            sensor_info.gyrX=event.values[0];
            sensor_info.gyrY=event.values[1];
            sensor_info.gyrZ=event.values[2];
        }
        if (type == Sensor.TYPE_LIGHT) {
            sensor_info.light=event.values[0];
        }
        if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
            sensor_info.laccX=event.values[0];
            sensor_info.laccY=event.values[1];
            sensor_info.laccZ=event.values[2];
        }
        if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            sensor_info.magX=event.values[0];
            sensor_info.magY=event.values[1];
            sensor_info.magZ=event.values[2];
        }
        if (type == Sensor.TYPE_ORIENTATION) {
            sensor_info.orX=event.values[0];
            sensor_info.orY=event.values[1];
            sensor_info.orZ=event.values[2];
        }
        if (type == Sensor.TYPE_PROXIMITY) {
            sensor_info.proximity=event.values[0];
        }
        if (type == Sensor.TYPE_ROTATION_VECTOR) {
            sensor_info.rotX=event.values[0];
            sensor_info.rotY=event.values[1];
            sensor_info.rotZ=event.values[2];
        }
        /*
        if (type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            TextView text10 = findViewById(R.id.textView10);
            text10.setText(String.format("Ambient Temperature : %1$s", event.values[0]));
        }

         */
    }//end onSensorChanged

    public void showDialog() {


        if( (Math.abs(sensor_info.accX)>shake_threshold) || (Math.abs(sensor_info.accY)>shake_threshold) || (Math.abs(sensor_info.accZ)>shake_threshold) ) {
            if(!shown_dialog) {
                shown_dialog = true;

                int stick = drawStick();
                String fortune = fortuneFor(stick);
                final AlertDialog.Builder viewDialog = new AlertDialog.Builder(this);
                viewDialog.setIcon(android.R.drawable.btn_star_big_on);
                viewDialog.setTitle("เซียมซีหมายเลข " + stick);
                viewDialog.setMessage(fortune);
                viewDialog.setPositiveButton(getString(R.string.shake_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                shown_dialog = false;
                            }
                        });
                viewDialog.show();
            }//end if
        }//end if
    }//end showDialog

    private final BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            int health= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
            String health_text = (health == 2) ? "GOOD" : "NOT GOOD";
            int level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
            String plugged_text = (plugged == 2) ? "USB" : (plugged == 1) ? "AC" : "UNPLUGGED";
            boolean present= intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
            int scale= intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
            int status= intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
            String status_text = (status == 3) ? "DISCHARGING" : (status == 2) ? "CHARGING" : "FULL";
            String technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            int temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            int voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);

            /*
            TextView text = findViewById(R.id.textView11);
            text.setText(   "Battery Info: \n"+
                    "Health: "+health_text+"\n"+
                    "Present: "+present+"\n"+
                    "Level: "+level+"  "+
                    "Scale: "+scale+"\n"+
                    "Plugged: "+plugged_text+"\n"+
                    "Status: "+status_text+"\n"+
                    "Technology: "+technology+"\n"+
                    "Temperature: "+temperature+"\n"+
                    "Voltage: "+voltage+"\n");

             */
        }//end onReceive
    };

    @SuppressLint("WakelockTimeout")
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),
                SensorManager.SENSOR_DELAY_NORMAL);

        this.registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if (!wl.isHeld()) {
            wl.acquire();
        }
        hdr.postDelayed(pollTask, POLL_INTERVAL);
    }//end onResume

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

        this.unregisterReceiver(this.batteryInfoReceiver);

        if (wl.isHeld()) {
            wl.release();
        }
        hdr.removeCallbacks(pollTask);
    }//end onPause

    static class SensorInfo{
        float accX, accY, accZ;
        float graX, graY, graZ;
        float gyrX, gyrY, gyrZ;
        float light;
        float laccX, laccY, laccZ;
        float magX, magY, magZ;
        float orX, orY, orZ;
        float proximity;
        float rotX, rotY, rotZ;
    }//end class SensorInfo

}//end MainActivity