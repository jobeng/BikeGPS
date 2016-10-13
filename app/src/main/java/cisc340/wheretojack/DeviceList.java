package cisc340.wheretojack;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.util.Set;
import java.util.ArrayList;

public class DeviceList extends Activity
{
    private static final String EXTRA_ADDRESS = "Address";
    Button btnPaired;
    ListView deviceList;

    //Bluetooth adapter on the the phone initialization
    private  BluetoothAdapter myBluetooth = null;

    //List of all paired devices - will only contain one device
    private Set pairedDevices;

    /*This method gets a list of all the paired devices with the app*/
    private void pairedDeviceList()
    {
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            //Get Device Name and Address (Only one in our case)
            for (BluetoothDevice bt : pairedDevices)
                list.add(bt.getName() + "\n" + bt.getAddress());
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Devices Found", Toast.LENGTH_LONG).show();
        }
        //Set Adapter to model how it will be viewed in the app
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(myListClickListener);

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener(){

        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            Intent i = new Intent(DeviceList.this, ledCompass.class);
            //Change the activity.
            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        //Initialize buttons
        btnPaired = (Button)findViewById(R.id.button2);
        deviceList= (ListView)findViewById(R.id.listView);

        //Initialize Bluetooth adapter
        //Finds detectable devices
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            finish();
        }
        else
        {
            if (myBluetooth.isEnabled())
            {}
            else
            {
                //Ask to turn on bluetooth
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
            }
        }

        //Bluetooth searching button method
        btnPaired.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                pairedDeviceList();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
