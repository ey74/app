package com.example.tyndall;

public class Const {
	
	/**
	 * CONST 
	 * 
	 */
	//Connection : TCP
	public static final int SERVER_PORT = 13123;
	public static final int NB_SOCKET_MAX = 4;
	//Connection : UDP
	public static final int UDP_PORT = 13456;
	public static final int RECEIVING_TIMEOUT = 10000;
	public static final int RECEIVING_TIMEOUT_SERVER = 30000;
	public static final byte[] PASSWORD = {'L','U','K','E'};
	
	
	//UI
	public static final long TIME_REFRESH = 10;
	public static final long TIME_REFRESH_GRAPH =4;//20
	
	
	
	/**
	 * 
	 * Packet structure
	 */
	public static int HEADER_DATA_LENGTH = 2; //packet size is written on 2bytes
	public static final int NO_SENSOR = 5;
	public static final int NO_SAMPLE_MAX = 256;
	public static final double MAX_ACCEL = 400.0f;
	
	public static final int MAX_SAMPLES_FOR_FFT = 64;
	public static final int FFT_CHART_LENGTH = 20;
	
	
	
	
	
	/******************************
	 *  // Instruction List
	 ******************************/
	
	public static final int INST_DATA_ZERO       =       0x00;
	public static final int INST_DATA_ONE        =       0x01;
	
	//System related
	public static final int INST_SYSTEM_RESET  =       0x00;
	
	//Network related
	public static final int INST_HEADER_TYPE =     0x01;
	
	//Power related
	public static final int INST_POWER_SAVE_MAIN =     0x02;
	public static final int INST_POWER_SAVE_WIFI =     0x03;
	public static final int INST_POWER_WIFI =      0x04;
	public static final int INST_BATTERY_LEVEL_ENABLE =      0x05;
	
	/**
	 * SENSOR RELATED
	 */
	
	//MPU
	
	
	
	public static final int INST_MPU_SAMPLING_FREQ = 0xB3;// Hz
	public static final int[] MPU_SAM_FRE_NO_FILTER = {0x00, 0x01, 0x02, 0x04, 0x09};//1000,800,400,200,100
	
	//Accelerometer MPU related
	public static final int ACC_MPU_ID               = 0x01;
	public static final int INST_ACC_MPU_ENABLE      = 0xA0;
	public static final int INST_ACC_MPU_AXES        = 0xA1;
	public static final int INST_ACC_MPU_RANGE       = 0xA2;
	public static final int INST_ACC_MPU_RESOLUTION  = 0xAF;
	public static final int[] ACC_MPU_RANGES = {0x00, 0x08, 0x10, 0x18};//2, 4 ,8 , 16 g
	
	//Gyroscope MPU related
	public static final int GYR_MPU_ID               = 0x02;
	public static final int INST_GYR_MPU_ENABLE      = 0xB0;
	public static final int INST_GYR_MPU_AXES        = 0xB1;
	public static final int INST_GYR_MPU_RANGE       = 0xB2;
	public static final int INST_GYR_MPU_RESOLUTION  = 0xB5;
	public static final int[] GYR_MPU_RANGES = {0x00, 0x08, 0x10, 0x18};//250, 500 ,1000 , 2000 
	
	//Magnetometer MPU related
	public static final int MAG_MPU_ID               = 0x03;
	public static final int INST_MAG_MPU_ENABLE      = 0xC0;
	public static final int INST_MAG_MPU_AXES        = 0xC1;
	public static final int INST_MAG_MPU_RANGE       = 0xC2;
	public static final int INST_MAG_MPU_RESOLUTION  = 0xC3;
	
	//FXO
	//Accelerometer FXO related
	public static final int ACC_FXO_ID               = 0x04;
	public static final int INST_ACC_FXO_ENABLE      = 0xD0;
	public static final int INST_ACC_FXO_AXES        = 0xD1;
	public static final int INST_ACC_FXO_RANGE       = 0xD2;
	public static final int INST_ACC_FXO_RESOLUTION  = 0xD3;
	
	//Magnetometer FXO related
	public static final int MAG_FXO_ID               = 0x05;
	public static final int INST_MAG_FXO_ENABLE      = 0xE0;
	public static final int INST_MAG_FXO_AXES        = 0xE1;
	public static final int INST_MAG_FXO_RANGE       = 0xE2;
	public static final int INST_MAG_FXO_RESOLUTION  = 0xE3;
	
	//LIS
	//Accelerometer LIS related
	public static final int ACC_LIS_ID               = 0x06;
	public static final int INST_ACC_LIS_ENABLE      = 0xF0;
	public static final int INST_ACC_LIS_AXES        = 0xF1;
	public static final int INST_ACC_LIS_RANGE       = 0xF2;
	public static final int INST_ACC_LIS_RESOLUTION  = 0xF3;
	
	
	
	/**
	 * Configuration List
	 */

	public static final int CONF_SET_TIME[] =  {0x00,0x01,0x08,'\0'};
	public static final int CONF_SET_IP_ADD[] = {0x00,0x02,0x04,'\0'};
	
	
	
	/**
	 * Packet extenders
	 */
	
	public static final int NOEXTEND = 0X00;
	public static final int EXTEND_COMMAND = 0x01;
	public static final int EXTEND_INSTRUCTION = 0x02;
	public static final int EXTEND_SENSOR_DATA = 0x03;
	public static final int EXTEND_ALGORITHM = 0x04;
	

}
