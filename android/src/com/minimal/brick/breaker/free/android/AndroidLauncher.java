package com.minimal.brick.breaker.free.android;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.minimal.brick.breaker.android.util.IabHelper;
import com.minimal.brick.breaker.android.util.IabResult;
import com.minimal.brick.breaker.android.util.Inventory;
import com.minimal.brick.breaker.android.util.Purchase;
import com.minimal.brick.breaker.free.ActionResolver;
import com.minimal.brick.breaker.free.MyGdxGame;

public class AndroidLauncher extends AndroidApplication implements ActionResolver{

	IabHelper mHelper;
    boolean mAdsRemoved = false;
	
	FrameLayout fLayout;
	AdView admobView, adView;
	protected View gameView;
	private InterstitialAd interstitialAd;
	ConnectivityManager connManager;
    private NetworkInfo info;
	
	private static final String BANNER_ID = "ca-app-pub-7775582829834874/3718178741";
	private static final String INTERSTITIAL_ID = "ca-app-pub-7775582829834874/5194911941";
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiz4xCldQgOS85WyJviHCfDEHvHILfBmBHViwwxmMx3sWOgnVXElWg8ZEsFp4ZgkhPcRb3ZqA6+xlDI4L0oY2L0+L95KuYFy5D45qVrllI7aDCro6hHG0/MmXaYGMU9VDzeohPpoW01LnUzcJ32Onr8JNT9wEnzxpH6YRhoF5JEhUgYRPR19nkvUx2bH1mWa2yQMBsU7cCAoG5AwMAsz1fRWJdDmDs6RJTu2g3hzVD1ueBIspeck4BxSLdKu5N1+PG0ZNlfnZ0b1XIHlBxt3fBLFRAW5qZlmBV/kuGmQoEkZiy9uKpc8124UFNehFYxulN0jKefYQ7PxcteaeDT9VFwIDAQAB";
		
		/******************************GOOGLE BILLING******************************/
		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			   public void onIabSetupFinished(IabResult result) {
			      if (!result.isSuccess()) {
			         // Oh noes, there was a problem.
			         Log.d("IAB", "Problem setting up In-app Billing: " + result);
			      }
			      // Hooray, IAB is fully set up!
			      Log.d("IAB", "Billing Success: " + result);
			      
			      processPurchases();
			   }
			});
		/**************************************************************************/
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		fLayout = new FrameLayout(this);
		FrameLayout.LayoutParams fParams = 
		new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
		                			FrameLayout.LayoutParams.MATCH_PARENT);
		fLayout.setLayoutParams(fParams);
		
		admobView = createAdView();
		View gameView = createGameView(config);

		fLayout.addView(gameView);
		fLayout.addView(admobView);
		setContentView(fLayout);
		startAdvertising(admobView);
		
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(INTERSTITIAL_ID);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
			}
			@Override
			public void onAdClosed() {
			}
		});
		showOrLoadInterstital();
		
		connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connManager.getActiveNetworkInfo();

	}
	
	private AdView createAdView() {
		adView = new AdView(this);
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(BANNER_ID);
		adView.setId(12345); // this is an arbitrary id, allows for relative positioning in createGameView()
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			adView.setLayoutParams(params);
			//adView.setBackgroundColor(Color.BLACK);
			return adView;
	}
	
	private View createGameView(AndroidApplicationConfiguration config) {
		gameView = initializeForView(new MyGdxGame(this), config);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.BELOW, adView.getId());
			gameView.setLayoutParams(params);
			return gameView;
	}

	public void startAdvertising(AdView adView) {
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}
	
	public void showOrLoadInterstital() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (info != null && info.isConnected()){
						if (interstitialAd.isLoaded()) {
							interstitialAd.show();
	
						} else {
							AdRequest interstitialRequest = new AdRequest.Builder().build();
							interstitialAd.loadAd(interstitialRequest);
						}
					}
				}
			});
		} catch (Exception e) {
		}
	 }
	
	public void LoadInterstital() {
			try {
				runOnUiThread(new Runnable() {
					public void run() {
						if (info != null && info.isConnected()){
							if (!interstitialAd.isLoaded()) {
								AdRequest interstitialRequest = new AdRequest.Builder().addTestDevice("1E55D4A762FAE18E36A0BC83CBF3FA2B").build();
								interstitialAd.loadAd(interstitialRequest);
							}
						}
					}
				});
			} catch (Exception e) {
			}
	}
	
	public void showAdsBottom() {
        runOnUiThread(new Runnable() {
                @Override
                public void run() {               	  
              	  if (info != null && info.isConnected()){
              		  adView.setVisibility(View.VISIBLE); 	
                  	  
                  	  adView.setLayoutParams(new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT, 
                                FrameLayout.LayoutParams.WRAP_CONTENT, 
                                Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM));
                     }
                }
        	});
	  }

	  public void showAdsTop() {
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
              	  if (info != null && info.isConnected()){
	                	  adView.setVisibility(View.VISIBLE);
	                	  
	                	  adView.setLayoutParams(new FrameLayout.LayoutParams(
	                              FrameLayout.LayoutParams.MATCH_PARENT, 
	                              FrameLayout.LayoutParams.WRAP_CONTENT, 
	                              Gravity.CENTER_HORIZONTAL | Gravity.TOP));
              	  }
                }
        	});
	  }
	  
	  public void hideAds() {
	          runOnUiThread(new Runnable() {
	                  @Override
	                  public void run() {
	                	  if (info != null && info.isConnected()){
	                		  adView.setVisibility(View.GONE);
	                	  } 
	                  }
	          });
	  }
	  
	  public int hauteurBanniere(){
		  if (info != null && info.isConnected()){
			  return adView.getHeight();
		  }
		  else return 0;		  
	  }
		
		public void removeAds(){
			System.out.println("TEST1 removeAds() = ");
			 mHelper.launchPurchaseFlow(this, SKU_REMOVE_ADS, RC_REQUEST,
				     mPurchaseFinishedListener, "HANDLE_PAYLOADS");
		}
		
		public boolean adsListener(){
			return mAdsRemoved;
		}
		
		// Callback for when a purchase is finished
	    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
	        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
				System.out.println("TEST2 removeAds() = ");
	            if ( purchase == null) return;
	            Log.d("IAB", "Purchase finished: " + result + ", purchase: " + purchase);
				System.out.println("TEST3 removeAds() = ");

	            // if we were disposed of in the meantime, quit.
	            if (mHelper == null) return;

	            if (result.isFailure()) {
	    			System.out.println("TEST4 removeAds() = ");
	                //complain("Error purchasing: " + result);
	                //setWaitScreen(false);
	                return;
	            }
//	            if (!verifyDeveloperPayload(purchase)) {
//	                //complain("Error purchasing. Authenticity verification failed.");
//	                //setWaitScreen(false);
//	                return;
//	            }

				System.out.println("TEST5 removeAds() = ");
	            Log.d("IAB", "Purchase successful.");

	            if (purchase.getSku().equals(SKU_REMOVE_ADS)) {
	                // bought the premium upgrade!
	    			System.out.println("TEST6 removeAds() = ");
	                Log.d("IAB", "Purchase is premium upgrade. Congratulating user.");

	                // Do what you want here maybe call your game to do some update
	                //
	            	// Maybe set a flag to indicate that ads shouldn't show anymore
	                mAdsRemoved = true;

	            }
				System.out.println("TEST7 removeAds() = ");
	        }
	    };

		public void processPurchases(){
			mHelper.queryInventoryAsync(mGotInventoryListener);
		}
		
		// Listener that's called when we finish querying the items and subscriptions we own
		IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
		        Log.d("IAB", "Query inventory finished.");

		        // Have we been disposed of in the meantime? If so, quit.
		        if (mHelper == null) return;

		        // Is it a failure?
		        if (result.isFailure()) {
		            // handle failure here
		            return;
		        }

		        // Do we have the premium upgrade?
		        Purchase removeAdPurchase = inventory.getPurchase(SKU_REMOVE_ADS);
		        mAdsRemoved = (removeAdPurchase != null);
		        System.out.println("TEST5 mAdsRemoved = " + mAdsRemoved + "*****************");
		        System.out.println("TEST6 removeAdPurchase = " + removeAdPurchase + "*****************");
		        //System.out.println("inventory.getSkuDetails(SKU_REMOVE_ADS).getPrice() = " + inventory.getSkuDetails(SKU_REMOVE_ADS).getPrice() + "*****************");
		    }
		};
		
		@Override
		public void onActivityResult(int request, int response, Intent data) {
		    super.onActivityResult(request, response, data);

		    if (mHelper != null) {
		        // Pass on the activity result to the helper for handling
		        if (mHelper.handleActivityResult(request, response, data)) {
		            Log.d("IAB", "onActivityResult handled by IABUtil.");
		        }
		    }
		}
		
		@Override
		public void onDestroy() {
		   super.onDestroy();
		   if (mHelper != null) mHelper.dispose();
		   mHelper = null;
		}
}
