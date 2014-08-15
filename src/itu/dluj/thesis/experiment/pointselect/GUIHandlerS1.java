package itu.dluj.thesis.experiment.pointselect;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import android.util.Log;

public class GUIHandlerS1 {

	private int screenWidth;
	private int screenHeight;
	private int iBtnToClick;
	private Point[][] pBtnsCoords;
	private boolean[] bBtnsClicked = new boolean[]{false,false};
	private int iBtnsClicked = 0;
	public boolean allClicked = false;
	public long endS1;
	public long startOfExperiment;

	public GUIHandlerS1(int width, int height, long milis){
		startOfExperiment = milis;
		screenWidth = width;
		screenHeight = height;
		iBtnToClick = 0;
		//fullScreenImg Coords
		pBtnsCoords = new Point[2][3];
		pBtnsCoords[0][0] = new Point(screenWidth*0.00, screenHeight*0.00);
		pBtnsCoords[0][1] = new Point(screenWidth*0.49, screenHeight);
		pBtnsCoords[0][2] = new Point(screenWidth*0.15, screenHeight*0.45);
		
		pBtnsCoords[1][0] = new Point(screenWidth*0.51, screenHeight*0.0);
		pBtnsCoords[1][1] = new Point(screenWidth, screenHeight);
		pBtnsCoords[1][2] = new Point(screenWidth*0.70, screenHeight*0.45);
		
	}
	
	/******************************** Drawing methods ****************************************/
	
	public Mat drawSquares(Mat mRgb){
		Mat rec = mRgb;
		if(bBtnsClicked[0] == true){
			Core.rectangle(rec, pBtnsCoords[0][0], pBtnsCoords[0][1], Tools.green, -1);
		}else{
			Core.rectangle(rec, pBtnsCoords[0][0], pBtnsCoords[0][1], Tools.blue, -1);
		}
		if(bBtnsClicked[1] == true){
			Core.rectangle(rec, pBtnsCoords[1][0], pBtnsCoords[1][1], Tools.green, -1);
		}else{
			Core.rectangle(rec, pBtnsCoords[1][0], pBtnsCoords[1][1], Tools.blue, -1);
		}
		
		rec = writeInfoToImage(rec, pBtnsCoords[0][2], "1");
		rec = writeInfoToImage(rec, pBtnsCoords[1][2], "2");

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
		if(click.inside(rect_one)){
			bBtnsClicked[iBtnToClick] = true;
			iBtnsClicked = iBtnsClicked + 1;
			iBtnToClick = iBtnToClick + 1;
			if(iBtnsClicked == 2){
				allClicked = true;
				endS1 = System.currentTimeMillis();
				Log.i("GUIHandlerS1", "endS1 - startOfExperiment :: "+ (endS1 - startOfExperiment)/1000 + " secs");
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
		bBtnsClicked = new boolean[]{false,false};
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
		Point point = new Point(screenWidth*0.05, screenHeight*0.95);
		Core.putText(mRgb, string, point, Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0,0,0), 5);
		Core.putText(mRgb, string, point, Core.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255,255,255), 1);
		return mRgb;
	}	
}
