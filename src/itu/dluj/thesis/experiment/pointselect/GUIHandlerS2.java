package itu.dluj.thesis.experiment.pointselect;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class GUIHandlerS2 {

	private int screenWidth;
	private int screenHeight;
	private int iBtnToClick;
	private Point[][] pBtnsCoords;
	private Rect[] rBtnsRoi;
	private boolean[] bBtnsClicked = new boolean[]{false,false,false, false,false,false};
	public boolean allClicked = true;
	private int iBtnsClicked = 0;

	public GUIHandlerS2(int width, int height){
		screenWidth = width;
		screenHeight = height;
		iBtnToClick = 0;
		//fullScreenImg Coords
		pBtnsCoords = new Point[6][3];
		//upper row
		pBtnsCoords[0][0] = new Point(screenWidth*0.05, screenHeight*0.05);
		pBtnsCoords[0][1] = new Point(screenWidth*0.28, screenHeight*0.45);
		pBtnsCoords[0][2] = new Point(screenWidth*0.15, screenHeight*0.25);
		
		pBtnsCoords[1][0] = new Point(screenWidth*0.38, screenHeight*0.05);
		pBtnsCoords[1][1] = new Point(screenWidth*0.61, screenHeight*0.45);
		pBtnsCoords[1][2] = new Point(screenWidth*0.48, screenHeight*0.25);
		
		pBtnsCoords[2][0] = new Point(screenWidth*0.71, screenHeight*0.05);
		pBtnsCoords[2][1] = new Point(screenWidth*0.95, screenHeight*0.45);
		pBtnsCoords[2][2] = new Point(screenWidth*0.81, screenHeight*0.25);
		//lower row
		pBtnsCoords[3][0] = new Point(screenWidth*0.05, screenHeight*0.55);
		pBtnsCoords[3][1] = new Point(screenWidth*0.28, screenHeight*0.95);
		pBtnsCoords[3][2] = new Point(screenWidth*0.15, screenHeight*0.75);
		
		pBtnsCoords[4][0] = new Point(screenWidth*0.38, screenHeight*0.55);
		pBtnsCoords[4][1] = new Point(screenWidth*0.61, screenHeight*0.95);
		pBtnsCoords[4][2] = new Point(screenWidth*0.48, screenHeight*0.75);
		
		pBtnsCoords[5][0] = new Point(screenWidth*0.71, screenHeight*0.55);
		pBtnsCoords[5][1] = new Point(screenWidth*0.95, screenHeight*0.95);
		pBtnsCoords[5][2] = new Point(screenWidth*0.81, screenHeight*0.75);

	}
	
	/******************************** Drawing methods ****************************************/
	
	public Mat drawSquares(Mat mRgb){
		Mat rec = mRgb.clone();
		for(int i = 0; i< 6; ++i){
			if(bBtnsClicked[i] == true){
				Core.rectangle(rec, pBtnsCoords[i][0], pBtnsCoords[i][1], Tools.green, -1);
			}else{
				Core.rectangle(rec, pBtnsCoords[i][0], pBtnsCoords[i][1], Tools.red, -1);
			}
			rec = writeInfoToImage(rec, pBtnsCoords[i][2], ""+(i+1));
		}

		Mat output = new Mat();
		Core.addWeighted(mRgb, 0.5, rec, 0.5, 0, output);

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
			iBtnsClicked  = iBtnsClicked + 1;
			if(iBtnsClicked == 3){
				allClicked = true;
			}
			return true;
		}

		return false;
	}
	
	/******************************* Utility methods ******************************************/
	
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
