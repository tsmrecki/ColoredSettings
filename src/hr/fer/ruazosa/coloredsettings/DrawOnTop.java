package hr.fer.ruazosa.coloredsettings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Calculates RGB values from the bitmaps taken with camera
 * @author Tomislav
 *
 */
public class DrawOnTop extends View {
	
	Bitmap mBitmap;
	Paint mPaintBlack;
	Paint mPaintYellow;
	Paint mPaintRed;
	Paint mPaintGreen;
	Paint mPaintBlue;
	Paint mCircleGreen;
	byte[] mYUVData;
	int[] mRGBData;
	int mImageWidth, mImageHeight;
	int[] mRedHistogram;
	int[] mGreenHistogram;
	int[] mBlueHistogram;
	double[] mBinSquared;
	public double imageRedMean, imageGreenMean, imageBlueMean;
	
	/**
	 * Draws information about the RGB values
	 * @param context Activity context
	 */
    public DrawOnTop(Context context) {
        super(context);
        
        /**
         * defines colors used for drawing
         */
        mPaintBlack = new Paint();
        mPaintBlack.setStyle(Paint.Style.FILL);
        mPaintBlack.setColor(Color.BLACK);
        mPaintBlack.setTextSize(25);
        
        mPaintYellow = new Paint();
        mPaintYellow.setStyle(Paint.Style.FILL);
        mPaintYellow.setColor(Color.YELLOW);
        mPaintYellow.setTextSize(25);
        
        mPaintRed = new Paint();
        mPaintRed.setStyle(Paint.Style.FILL);
        mPaintRed.setColor(Color.RED);
        mPaintRed.setTextSize(25);
        
        mPaintGreen = new Paint();
        mPaintGreen.setStyle(Paint.Style.FILL);
        mPaintGreen.setColor(Color.GREEN);
        mPaintGreen.setTextSize(25);
        
        mPaintBlue = new Paint();
        mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.BLUE);
        mPaintBlue.setTextSize(25);
        
        mCircleGreen = new Paint();
        mCircleGreen.setStyle(Paint.Style.STROKE);
        mCircleGreen.setColor(Color.GREEN);
        mCircleGreen.setTextSize(20);
        mCircleGreen.setStrokeWidth(3);
        mCircleGreen.setAntiAlias(true);
        
        mBitmap = null;
        mYUVData = null;
        mRGBData = null;
        mRedHistogram = new int[256];
        mGreenHistogram = new int[256];
        mBlueHistogram = new int[256];
        mBinSquared = new double[256];
        for (int bin = 0; bin < 256; bin++)
        {
        	mBinSquared[bin] = ((double)bin) * bin;
        } // bin
    }
    
    /**
     * calculates values of RGB in real time
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null)
        {
        	int canvasWidth = canvas.getWidth();
        	int canvasHeight = canvas.getHeight();
        	int newImageWidth = canvasWidth;
        	int newImageHeight = canvasHeight;
        	int marginWidth = (canvasWidth - newImageWidth)/2;
        	        	
        	// Convert from YUV to RGB
        	decodeYUV420SP(mRGBData, mYUVData, mImageWidth, mImageHeight);
        	
        	// Calculate histogram
        	calculateIntensityHistogram(mRGBData, mRedHistogram, 
        			mImageWidth, mImageHeight, 0);
        	calculateIntensityHistogram(mRGBData, mGreenHistogram, 
        			mImageWidth, mImageHeight, 1);
        	calculateIntensityHistogram(mRGBData, mBlueHistogram, 
        			mImageWidth, mImageHeight, 2);
        	
        	// Calculate mean
        	
        	double redHistogramSum = 0, greenHistogramSum = 0, blueHistogramSum = 0;
        	for (int bin = 0; bin < 256; bin++)
        	{
        		imageRedMean += mRedHistogram[bin] * bin;
        		redHistogramSum += mRedHistogram[bin];
        		imageGreenMean += mGreenHistogram[bin] * bin;
        		greenHistogramSum += mGreenHistogram[bin];
        		imageBlueMean += mBlueHistogram[bin] * bin;
        		blueHistogramSum += mBlueHistogram[bin];
        	} // bin
        	imageRedMean /= redHistogramSum;
        	imageGreenMean /= greenHistogramSum;
        	imageBlueMean /= blueHistogramSum;
        	
        	/*
        	// Draw mean
        	String imageMeanStr = "Mean (R,G,B): " + String.format("%.4g", imageRedMean) + ", " + String.format("%.4g", imageGreenMean) + ", " + String.format("%.4g", imageBlueMean);
        	canvas.drawText(imageMeanStr, marginWidth+10-1, 30-1, mPaintBlack);
        	canvas.drawText(imageMeanStr, marginWidth+10+1, 30-1, mPaintBlack);
        	canvas.drawText(imageMeanStr, marginWidth+10+1, 30+1, mPaintBlack);
        	canvas.drawText(imageMeanStr, marginWidth+10-1, 30+1, mPaintBlack);
        	canvas.drawText(imageMeanStr, marginWidth+10, 30, mPaintYellow);
        	*/
        	
        	//Draw center circle
        	canvas.drawCircle(canvasWidth/2, canvasHeight/2, (int)(canvasWidth*0.4), mCircleGreen);
        	
        	
        } // end if statement
        
        super.onDraw(canvas);
        
    } // end onDraw method

    /**
     * decodes YUV420 bitmap to RGB information
     * @param rgb
     * @param yuv420sp
     * @param width Viewing screen width
     * @param height Viewing screen height
     */
	static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
    	final int frameSize = width * height;
    	
    	for (int j = 0, yp = 0; j < height; j++) {
    		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
    		for (int i = 0; i < width; i++, yp++) {
    			int y = (0xff & ((int) yuv420sp[yp])) - 16;
    			if (y < 0) y = 0;
    			if ((i & 1) == 0) {
    				v = (0xff & yuv420sp[uvp++]) - 128;
    				u = (0xff & yuv420sp[uvp++]) - 128;
    			}
    			
    			int y1192 = 1192 * y;
    			int r = (y1192 + 1634 * v);
    			int g = (y1192 - 833 * v - 400 * u);
    			int b = (y1192 + 2066 * u);
    			
    			if (r < 0) r = 0; else if (r > 262143) r = 262143;
    			if (g < 0) g = 0; else if (g > 262143) g = 262143;
    			if (b < 0) b = 0; else if (b > 262143) b = 262143;
    			
    			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
    		}
    	}
    }
    
	
	/**
	 * decodes grayscale YUV430SP values to RGB
	 * @param rgb
	 * @param yuv420sp
	 * @param width Viewing screen width
	 * @param height Viewing screen height
	 */
    static public void decodeYUV420SPGrayscale(int[] rgb, byte[] yuv420sp, int width, int height)
    {
    	final int frameSize = width * height;
    	
    	for (int pix = 0; pix < frameSize; pix++)
    	{
    		int pixVal = (0xff & ((int) yuv420sp[pix])) - 16;
    		if (pixVal < 0) pixVal = 0;
    		if (pixVal > 255) pixVal = 255;
    		rgb[pix] = 0xff000000 | (pixVal << 16) | (pixVal << 8) | pixVal;
    	} // pix
    }
    
    /**
     * calculates intensity histogram to be used for calculating RGB values
     * @param rgb
     * @param histogram
     * @param width Viewing screen width
     * @param height Viewing screen height
     * @param component Defines red, green or blue component
     */
    static public void calculateIntensityHistogram(int[] rgb, int[] histogram, int width, int height, int component)
    {
    	for (int bin = 0; bin < 256; bin++)
    	{
    		histogram[bin] = 0;
    	} // bin
    	if (component == 0) // red
    	{
    		for (int pix = 0; pix < width*height; pix += 3)
    		{
	    		int pixVal = (rgb[pix] >> 16) & 0xff;
	    		histogram[ pixVal ]++;
    		} // pix
    	}
    	else if (component == 1) // green
    	{
    		for (int pix = 0; pix < width*height; pix += 3)
    		{
	    		int pixVal = (rgb[pix] >> 8) & 0xff;
	    		histogram[ pixVal ]++;
    		} // pix
    	}
    	else // blue
    	{
    		for (int pix = 0; pix < width*height; pix += 3)
    		{
	    		int pixVal = rgb[pix] & 0xff;
	    		histogram[ pixVal ]++;
    		} // pix
    	}
    }
} 