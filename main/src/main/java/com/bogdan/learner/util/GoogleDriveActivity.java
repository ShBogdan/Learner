package com.bogdan.learner.util;

public class GoogleDriveActivity {
// {extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
//    final int RESOLVE_CONNECTION_REQUEST_CODE = 66699;
//    GoogleApiClient mGoogleApiClient;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        switch (requestCode) {
//            case RESOLVE_CONNECTION_REQUEST_CODE:
//                if (resultCode == RESULT_OK) {
//                    mGoogleApiClient.connect();
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        if (connectionResult.hasResolution()) {
//            try {
//                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
//            } catch (IntentSender.SendIntentException e) {
//                // Unable to resolve, message user appropriately
//            }
//        } else {
//            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
//        }
//
//    }
}
