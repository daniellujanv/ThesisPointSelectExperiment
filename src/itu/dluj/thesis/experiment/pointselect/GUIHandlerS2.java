package itu.dluj.thesis.experiment.pointselect;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import android.util.Log;

public class GUIHandlerS2 {

	private int screenWidth;
	private int screenHeight;
	private int iBtnToClick;
	private Point[][] pBtnsCoords;
	private boolean[] bBtnsClicked = new boolean[]{false,false,false, false,false,false};
	private int iBtnsClicked = 0;
	public boolean allClicked = false;
	public long endS2;
	public long startOfExperiment;

	public GUIHandlerS2(int width, int height, long milis){
		startOfExperiment = milis;
		screenWidth = width;
		screenHeight = height;
		iBtnToClick = 0;
		//fullScreenImg Coords
		pBtnsCoords = new Point[6][3];
		//upper row
		pBtnsCoords[0][0] = new Point(screenWidth*0.0, screenHeight*0.0);
		pBtnsCoords[0][1] = new Point(screenWidth*0.32, screenHeight*0.49);
		pBtnsCoords[0][2] = new Point(screenWidth*0.15, screenHeight*0.25);
		
		pBtnsCoords[1][0] = new Point(screenWidth*0.33, screenHeight*0.0);
		pBtnsCoords[1][1] = new Point(screenWidth*0.65, screenHeight*0.49);
		pBtnsCoords[1][2] = new Point(screenWidth*0.48, screenHeight*0.25);
		
		pBtnsCoords[2][0] = new Point(screenWidth*0.66, screenHeight*0.0);
		pBtnsCoords[2][1] = new Point(screenWidth, screenHeight*0.49);
		pBtnsCoords[2][2] = new Point(screenWidth*0.81, screenHeight*0.25);
		//lower row
		pBtnsCoords[3][0] = new Point(screenWidth*0.0, screenHeight*0.50);
		pBtnsCoords[3][1] = new Point(screenWidth*0.32, screenHeight);
		pBtnsCoords[3][2] = new Point(screenWidth*0.15, screenHeight*0.75);
		
		pBtnsCoords[4][0] = new Point(screenWidth*0.33, screenHeight*0.50);
		pBtnsCoords[4][1] = new Point(screenWidth*0.65, screenHeight);
		pBtnsCoords[4][2] = new Point(screenWidth*0.48, screenHeight*0.75);
		
		pBtnsCoords[5][0] = new Point(screenWidth*0.66, screenHeight*0.50);
		pBtnsCoords[5][1] = new Point(screenWidth, screenHeight);
		pBtnsCoords[5][2] = new Point(screenWidth*0.81, screenHeight*0.75);

	}
	
	/******************************** Drawing methods ****************************************/
	
	public Mat drawSquares(Mat mRgb){
		Mat rec = mRgb.clone();
		for(int i = 0; i< 6; ++i){
			if(bBtnsClicked[i] == true){
				Core.rectangle(rec, pBtnsCoords[i][0], pBtnsCoords[i][1], Tools.green, -1);
			}else{
				Core.rectangle(rec, pBtnsCoords[i][0], pBtnsCoords[i][1], Tools.blue, -1);
			}
			rec = writeInfoToImage(rec, pBtnsCoords[i][2], ""+(i+1));
		}

		Mat output = new Mat();
		Core.addWeighted(mRgb, 0, rec, 1.0, 0, output);

		return output;
	}
	
	
	/******************************* Action methods *******************************************/
	/*
	 * onClick 
	 * returns true if click is accepted (clicked on something)
	 */
	public boolean onClick(Point click){
		/*
		 * - if iCurrentPatient == -1 --> we are in PatientSelection
		 * 		- check in which patient the click is inside of
		 * Coords [2] == upper left outer rectangle
		 * Coords [3] == lower right outer rectangle
		 */
		Rect rect_one = new Rect(pBtnsCoords[iBtnToClick][0], pBtnsCoords[iBtnToClick][1]);
		Log.i("GUIHandlerS2", "iBtnToClick::"+iBtnToClick);
		if(click.inside(rect_one)){
			Log.i("GUIHandlerS2", "acceptedClick::"+iBtnToClick);
			bBtnsClicked[iBtnToClick] = true;
			iBtnsClicked  = iBtnsClicked + 1;
			iBtnToClick = iBtnToClick + 1;
			if(iBtnsClicked == 6){
				allClicked = true;
				endS2 = System.currentTimeMillis();
				Log.i("GUIHandlerS2", "endS2 - startOfExperiment :: "+ (endS2 - startOfExperiment)/1000 + " secs");
			}
			return true;
		}

		return false;
	}
	
	/******************************* Utility methods ******************************************/
	public void reset(){
		iBtnToClick = 0;
		iBtnsClicked = 0;
		allClicked = false;
		bBtnsClicked = new boolean[]{false,false,false, false,false,false};
	}
	
	
	/*
	 * Utility method - writes to color image
	 */
	public Mat writeInfoToImage(Mat mRgb, Point point, final String string) {
		Core.putText(mRgb, string, point, Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0,0,0), 5);
		Core.putText(mRgb, string, point, Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255,255,255), 1);
		return mRgb;
	}
	
	/*
	 * Utility method - writes to color image
	 */
	public Mat writeInfoToImage(Mat mRgb, final String string) {
		Point point = new Point();
		Core.putText(mRgb, string, point, Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0,0,0), 5);
		Core.putText(mRgb, string, point, Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255,255,255), 1);
		return mRgb;
	}	
}
