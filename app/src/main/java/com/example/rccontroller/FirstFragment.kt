package com.example.rccontroller

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private val REQUEST_CONNECT_DEVICE_SECURE = 1
    private val REQUEST_CONNECT_DEVICE_INSECURE = 2
    private val REQUEST_ENABLE_BT: Int = 3
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val scannedDeviceArrayList = mutableListOf<String>()
    private lateinit var scannedDeviceAdapter: ArrayAdapter<String>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // check if the device has bluetooth module
        if(bluetoothAdapter == null)
        {
            Toast.makeText(activity, "Bluetooth Device Not Available", Toast.LENGTH_LONG).show()

        }else{
            if (!bluetoothAdapter.isEnabled){

                // if bluetooth is not enabled, ask the user to turn the bluetooth on
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                // REQUEST_ENABLE_BT is any integer larger than 0. Defined as a private variable
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)

            }else{
                Toast.makeText(activity, "Bluetooth is On", Toast.LENGTH_LONG).show()
            }
        }

        view.findViewById<Button>(R.id.go_to_control_panel).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        // scan available nearby bluetooth devices
        val scannedDevices = view.findViewById<ListView>(R.id.discovered_device_listview)
        val scanDevicesBtn = view.findViewById<Button>(R.id.discover_device_btn)

        scanDevicesBtn.setOnClickListener {
            bluetoothAdapter?.startDiscovery()
            Toast.makeText(activity, "start discovering nearby device", Toast.LENGTH_LONG).show()
        }
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        println("filter is $filter")
        //need requireActivity() cuz all these methods and variables belong to Activity class
        requireActivity().registerReceiver(receiver, filter)
        scannedDeviceAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, scannedDeviceArrayList)
        scannedDevices.adapter = scannedDeviceAdapter


        // get paired devices
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val pairedDeviceListView = view.findViewById<ListView>(R.id.paired_device_listview)
        val pairedDeviceNames = mutableListOf<String>()
        if(bluetoothAdapter!!.isEnabled){
            pairedDevices?.forEach {
                device ->
                    run {
                        pairedDeviceNames.add(device.name)
                    }
            }
            val adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, pairedDeviceNames)
            adapter.notifyDataSetChanged()
            pairedDeviceListView.adapter = adapter
        }


//        how to pass values to TextView component in .xml file
//        val textView: TextView = view.findViewById<TextView>(R.id.test_text_view) as TextView
//        textView.text = "this is from FirstFragment.kt"
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action){
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice object and its info from the Intent.
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    scannedDeviceArrayList.add(device!!.name)
                    println("################################################")
                    println(scannedDeviceArrayList)
                    println("################################################")
                    scannedDeviceAdapter!!.notifyDataSetChanged()
                }
            }

        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//
//        requireActivity().unregisterReceiver(receiver)
//    }
}