package itu.dluj.thesis.experiment.pointselect;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class PointSelectExperiment {

	private boolean bShowingHand = false;
	private MatOfPoint mHandContour;
	private List<MatOfPoint> lHandContour;
	private Point handContourCentroid;
	private int screenWidth;
	private int screenHeight;
	private int screenArea;
	private Mat mRgb;
	private Mat mHsv;
	private MatOfInt convexHull;
	private MatOfInt4 convexityDefects;
	private List<Point[]> lFinalDefects;
	private Mat kernelErode;
	private Mat kernelDilate;
	private GUIHandlerS1 guiHandlerS1;
	private GUIHandlerS2 guiHandlerS2;
	private GUIHandlerS3 guiHandlerS3;
	private final Scalar minRange = new Scalar(120, 0, 0);
	private final Scalar maxRange = new Scalar(178, 255, 255);
	private Mat mBin;
	private double pctMinAreaGesture = 0.05;
	private double pctMaxAreaGesture = 0.28;
//	private boolean[] screenShowing;
	private Point lastPointedLocation;

	private boolean bInitPointSelect = true;

	public PointSelectExperiment(){
		screenWidth = 720/2;
		screenHeight = 480/2;
		screenArea = screenWidth*screenHeight;

//		screenShowing = new boolean[]{false, false, false};

		mRgb = new Mat();
		mHsv = new Mat();
		mBin = new Mat();


		convexHull = new MatOfInt();
		mHandContour = new MatOfPoint();
		convexityDefects = new MatOfInt4();
		lHandContour = new ArrayList<MatOfPoint>();
		lFinalDefects = new ArrayList<Point[]>();

		int kernelSizeE = 15;
		int kernelSizeD = 12;
		kernelErode = Mat.ones(kernelSizeE, kernelSizeE, CvType.CV_8U);
		kernelDilate = Mat.ones(kernelSizeD, kernelSizeD, CvType.CV_8U);

		guiHandlerS1 = new GUIHandlerS1(screenWidth, screenHeight);
		guiHandlerS2 = new GUIHandlerS2(screenWidth, screenHeight);
		guiHandlerS3 = new GUIHandlerS3(screenWidth, screenHeight);

	}

	public Mat handleFrame(Mat mFrame) {
		//    	Log.i("check", "handleFrame - init");
		Imgproc.cvtColor(mFrame, mRgb, Imgproc.COLOR_RGBA2RGB);
		//if 5 seconds passed with no change go back to "zipou"

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		//		Imgproc.medianBlur(mRgb, mRgb, 3);
		//		Imgproc.GaussianBlur(mRgb, mRgb, blurSize, 2.0);
		Imgproc.cvtColor(mRgb, mHsv, Imgproc.COLOR_RGB2HSV);

		//		Imgproc.medianBlur(mHsv, mHsv, 3);
		Core.inRange(mHsv, minRange, maxRange, mBin);
		//		Imgproc.threshold(mHsv, mBin, 1, 255, Imgproc.THRESH_BINARY);
		Imgproc.erode(mBin, mBin, kernelErode);
		Imgproc.dilate(mBin, mBin, kernelDilate);

		Imgproc.findContours(mBin, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		if(!bShowingHand){
			mRgb = Mat.zeros(mRgb.size(), mRgb.type());
		}
		mRgb = drawGUI(mRgb);
		double biggestArea = 0;
		int indexBiggestArea = -1;
		double tempArea = 0;
		for(int i=0; i< contours.size(); i++){
			tempArea = Imgproc.contourArea(contours.get(i));
			/******
			 * - the border of the frame is padded with 0's and 
			 * since the threshold is inverse it gets marked as a contour
			 * with an area equal to the screen area, therefore if tempArea == screenArea we don't count the contour
			 * - the contour has to cover over 30% of the screen area 
			 */
			if((tempArea >= biggestArea) && (tempArea != screenArea)){
				indexBiggestArea = i;
				biggestArea = tempArea;
			}
		}

		if(indexBiggestArea != -1){
			Log.i("check", "procressImage init");
			mHandContour = contours.get(indexBiggestArea);
			//			Imgproc.drawContours(mRgb, contours, -1, Tools.red, 2);

			double contourArea = Imgproc.contourArea(mHandContour);
			//		Log.i("ImageInteraction", "contour area:: " + contourArea + " screen area min::"+ screenArea*0.15
			//				+ "screen area max::"+ screenArea * 0.45);
			if(contourArea < screenArea * pctMinAreaGesture){
				//no good contours found
				mRgb = guiHandlerS1.writeInfoToImage(mRgb, "Hand too far!");
			}else if(contourArea > screenArea * pctMaxAreaGesture){
				//no good contours found
				mRgb = guiHandlerS1.writeInfoToImage(mRgb, "Hand too close!");
			}else{
				//good contour found
				//approximate polygon to hand contour, makes the edges more stable
				MatOfPoint2f temp_contour = new MatOfPoint2f(mHandContour.toArray());
				double epsilon = Imgproc.arcLength(temp_contour, true)*0.0035;
				MatOfPoint2f result_temp_contour = new MatOfPoint2f();
				Imgproc.approxPolyDP(temp_contour, result_temp_contour, epsilon, true);
				mHandContour = new MatOfPoint(result_temp_contour.toArray());

				temp_contour.release();
				result_temp_contour.release();

				lHandContour.add(mHandContour);
				handContourCentroid = Tools.getCentroid(mHandContour);
				//draw circle in centroid of contour
				Core.circle(mRgb, handContourCentroid, 5, Tools.red, -1);
				//            Log.i("contours-info", "contours="+contours.size()+" size="+biggestArea);
				/* 
				 * handContour == biggestContour 
				 * but Imgproc.drawContours method takes only List<MapOfPoint> as parameter
				 */
				Imgproc.drawContours(mRgb, lHandContour, -1, Tools.green, 1);
				Imgproc.convexHull(mHandContour, convexHull, true);
				Imgproc.convexityDefects(mHandContour, convexHull, convexityDefects);
				lFinalDefects = Gestures.filterDefects(convexityDefects, mHandContour);
				mRgb = Tools.drawDefects(mRgb, lFinalDefects, handContourCentroid);

				detectGesture(handContourCentroid, lFinalDefects);
//				//        	Log.i("check", "handleFrame - biggestArea found");
//				if(screenShowing[0] == false){
//					//showing screen 0 == 3 buttons
//				}else if(screenShowing[1] == false){
//					//showing screen 1 = 6 buttons
//				}else if(screenShowing[2] == false){
//					//showing screen 2 = 10 buttons
//				}
			}
		}else{
			mRgb = guiHandlerS1.writeInfoToImage(mRgb,"No contour found");
			//        	Log.i("check", "writting to image - nothing found");
		}


		lHandContour.clear();
		lFinalDefects.clear();
		mHandContour.release();
		mHsv.release();
		mBin.release();
		hierarchy.release();
		contours.clear();

		//    	Log.i("check", "handleFrame - end");
		//		return mBin;
		//		return mHsv;
		return mRgb;

	}

	private Mat drawGUI(Mat mRgb) {
		//draw things after converting image to hsv so they don't interfere with gestures
		if(guiHandlerS1.allClicked == false){
			mRgb = guiHandlerS1.drawSquares(mRgb);
			Log.i("PointSelectExperiment", "Drawing screen 1");
		}else if(guiHandlerS2.allClicked == false){
			mRgb = guiHandlerS2.drawSquares(mRgb);
			Log.i("PointSelectExperiment", "Drawing screen 2");
		}else if(guiHandlerS3.allClicked == false){
			mRgb = guiHandlerS3.drawSquares(mRgb);
			Log.i("PointSelectExperiment", "Drawing screen 3");
		}
		return mRgb;
	}

	/*******************gestures methods**************************/

	private void detectGesture(Point centroid, List<Point[]> lDefects) {
		if(lastPointedLocation != null){
			Core.circle(mRgb, lastPointedLocation, 5, Tools.magenta, -1);
		}
		if(bInitPointSelect){
			Point detectedPoint = Gestures.detectPointSelectGesture(lDefects, centroid, false); 
			if(detectedPoint != null){
				//			if(detectPointSelectGesture(convexityDefects, mHandContour, false) == true){
				//						postToast("PointSelect!");					
				if(bShowingHand == true){
					lastPointedLocation = detectedPoint;
				}else{
					if(detectedPoint.y > 288){
						detectedPoint.y = 288;
					}
					lastPointedLocation = detectedPoint;
					lastPointedLocation.y = lastPointedLocation.y * 1.6666666666666667;
				}
				bInitPointSelect = false;
				return;
			}
		}else{
			Point detectedPoint = Gestures.detectPointSelectGesture(lDefects, centroid, true);
			if(detectedPoint != null){
				if(guiHandlerS1.allClicked == false && guiHandlerS1.onClick(lastPointedLocation) == true){
					bInitPointSelect = true;
					return;
				}
			}
			detectedPoint = Gestures.detectPointSelectGesture(lDefects, centroid, false); 
			if(detectedPoint != null){
				//			if(detectPointSelectGesture(convexityDefects, mHandContour, false) == true){
				lastPointedLocation = detectedPoint;
//				Core.circle(mRgb, lastPointedLocation, 5, Tools.blue, -1);
				bInitPointSelect = false;
				return;
			}
		}
	}



	public Point getHandCentroid(){
		if(handContourCentroid != null){
			//			Log.i("StatesHandler", "centroidScaled::"+handContourCentroid.toString());
			return new Point(handContourCentroid.x*2, handContourCentroid.y*2);
		}
		return null;
	}
}
