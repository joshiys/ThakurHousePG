package com.example.thakurhousepg;


import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


//region Service Interfaces

interface RoomsService {
    @GET("rooms")
    Call<List<DataModel.Bed>> listRooms();

    @PUT("rooms/{number}")
    Call<DataModel.Bed> addRoom(@Path("number") String roomNumber, @Body DataModel.Bed room);

}

interface TenantService {
    @GET("tenants")
    Call<List<DataModel.Tenant>> getAllTenants();

    @POST("tenants/{id}")
    Call<DataModel.Tenant> updateTenant(@Path("id") String id, @Body DataModel.Tenant tenant);

    @PUT("/tenants")
    Call<DataModel.Tenant> addTenant(@Body DataModel.Tenant tenant);
}

interface BookingService {
    @GET("bookings")
    Call<List<DataModel.Booking>> getAllBookings();

    @POST("bookings/{id}")
    Call<DataModel.Booking> updateBooking(@Path("id") String id, @Body DataModel.Booking bookingInfo);

    @PUT("/bookings")
    Call<DataModel.Booking> createBooking(@Body DataModel.Booking bookingInfo);
}

interface PendingService {
    @GET("pending")
    Call<List<DataModel.Pending>> getAllPendingEntries();

    @POST("pending/{id}")
    Call<DataModel.Pending> updatePendingEntry(@Path("id") String id, @Body DataModel.Pending pendingEntryInfo);

    @PUT("/pending")
    Call<DataModel.Pending> createPendingEntry(@Body DataModel.Pending pendingEntry);
}

interface ReceiptService {
    @GET("receipts")
    Call<List<DataModel.Receipt>> getAllReceipts();

    @POST("receipts/{id}")
    Call<DataModel.Receipt> updateReceipt(@Path("id") String id, @Body DataModel.Receipt receiptInfo);

    @PUT("/receipts")
    Call<DataModel.Receipt> createReceipt(@Body DataModel.Receipt receipt);
}

//endregion

interface NetworkDataModulCallback<T> {
    void onSuccess(T obj);
    void onFailure();
    default void onResult() {
        System.out.println("Default OnResult implementation");
    }
}

public class NetworkDataModule {
    //<editor-fold desc="Variables">
    private static final String TAG = "NetworkDataModule";
    private static NetworkDataModule _instance = null;

    private HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private Retrofit retrofit = null;

    private ArrayList<DataModel.Bed> roomsList = new ArrayList<DataModel.Bed>();
    private ArrayList<DataModel.Tenant> tenantsList = new ArrayList<DataModel.Tenant>();
    private ArrayList<DataModel.Booking> bookingsList = new ArrayList<DataModel.Booking>();
    private ArrayList<DataModel.Pending> pendingList = new ArrayList<DataModel.Pending>();
    private ArrayList<DataModel.Receipt> receiptsList = new ArrayList<DataModel.Receipt>();

    private RoomsService roomsService = null;
    private TenantService tenantService = null;
    private BookingService bookingService = null;
    private PendingService pendingService = null;
    private ReceiptService receiptsService = null;

    private int initialDataFetchCompletionCount = 0;
    public NetworkDataModulCallback initialDataFetchCompletionCallBack;
    //</editor-fold>

    public static NetworkDataModule getInstance() {
        if (_instance == null) {
            _instance = new NetworkDataModule();
        }

        return _instance;
    }

    private NetworkDataModule() {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        roomsService = retrofit.create(RoomsService.class);
        tenantService = retrofit.create(TenantService.class);
        bookingService = retrofit.create(BookingService.class);
        pendingService = retrofit.create(PendingService.class);
        receiptsService = retrofit.create(ReceiptService.class);

        init();
    }

    private void init() {
        retrieveRoomsList();
        retrieveTenantsList();
        retrieveBookings();
        retrievePendingEntries();
        retrieveReceipts();
    }

    //region Retirve Initial resources
    private void retrieveRoomsList() {
        Call<List<DataModel.Bed>> roomsCall = roomsService.listRooms();

        roomsCall.enqueue(new Callback<List<DataModel.Bed>>() {
            @Override
            public void onResponse(Call<List<DataModel.Bed>> call, Response<List<DataModel.Bed>> response) {
                roomsList.clear();
                roomsList.addAll(response.body());
                for (DataModel.Bed bed : roomsList) {
                    Log.d(TAG, bed.toString());
                }

                invokeInitialDataFetchComplettionCallback();
            }

            @Override
            public void onFailure(Call<List<DataModel.Bed>> call, Throwable t) {
                Log.i(TAG, "Call to Rest Service failed");
            }
        });
    }

    private void retrieveBookings() {
        Call<List<DataModel.Booking>> roomsCall = bookingService.getAllBookings();

        roomsCall.enqueue(new Callback<List<DataModel.Booking>>() {
            @Override
            public void onResponse(Call<List<DataModel.Booking>> call, Response<List<DataModel.Booking>> response) {
                bookingsList.clear();
                bookingsList.addAll(response.body());
                for (DataModel.Booking booking : bookingsList) {
                    Log.d(TAG, booking.toString());
                }

                invokeInitialDataFetchComplettionCallback();
            }

            @Override
            public void onFailure(Call<List<DataModel.Booking>> call, Throwable t) {
                Log.i(TAG, "Call to Rest Service failed");
            }
        });
    }

    private void retrieveTenantsList() {
        Call<List<DataModel.Tenant>> tenantsCall = tenantService.getAllTenants();

        tenantsCall.enqueue(new Callback<List<DataModel.Tenant>>() {
            @Override
            public void onResponse(Call<List<DataModel.Tenant>> call, Response<List<DataModel.Tenant>> response) {
                tenantsList.clear();
                tenantsList.addAll(response.body());
                for (DataModel.Tenant tenant : tenantsList) {
                    Log.d(TAG, tenant.toString());
                }

                invokeInitialDataFetchComplettionCallback();
            }

            @Override
            public void onFailure(Call<List<DataModel.Tenant>> call, Throwable t) {
                Log.i(TAG, "Call to Tenant Service failed");
            }
        });
    }

    private void retrievePendingEntries() {
        Call<List<DataModel.Pending>> pendingCall = pendingService.getAllPendingEntries();

        pendingCall.enqueue(new Callback<List<DataModel.Pending>>() {
            @Override
            public void onResponse(Call<List<DataModel.Pending>> call, Response<List<DataModel.Pending>> response) {
                pendingList.clear();
                pendingList.addAll(response.body());
                for (DataModel.Pending pending : pendingList) {
                    Log.d(TAG, pending.toString());
                }

                invokeInitialDataFetchComplettionCallback();
            }

            @Override
            public void onFailure(Call<List<DataModel.Pending>> call, Throwable t) {
                Log.i(TAG, "Call to Tenant Service failed");
            }
        });
    }

    private void retrieveReceipts() {
        Call<List<DataModel.Receipt>> getReceiptsCall = receiptsService.getAllReceipts();

        getReceiptsCall.enqueue(new Callback<List<DataModel.Receipt>>() {
            @Override
            public void onResponse(Call<List<DataModel.Receipt>> call, Response<List<DataModel.Receipt>> response) {
                receiptsList.clear();
                receiptsList.addAll(response.body());
                for (DataModel.Receipt receipt : receiptsList) {
                    Log.d(TAG, receipt.toString());
                }

                invokeInitialDataFetchComplettionCallback();
            }

            @Override
            public void onFailure(Call<List<DataModel.Receipt>> call, Throwable t) {
                Log.i(TAG, "Call to Receipt Service failed" );
            }
        });
    }
    //endregion

    public boolean isInitialDataFetchComplete() {
        return initialDataFetchCompletionCount == 5;
    }

    public void invokeInitialDataFetchComplettionCallback() {
        initialDataFetchCompletionCount ++;

        if (isInitialDataFetchComplete() && initialDataFetchCompletionCallBack != null) {
            initialDataFetchCompletionCallBack.onResult();
        }
    }

    public ArrayList<DataModel.Bed> getRoomsList() {
        return roomsList;
    }

    public ArrayList<DataModel.Tenant> getAllTenants(boolean onlyCurrent) {
        ArrayList<DataModel.Tenant> list = new ArrayList<DataModel.Tenant>();
        if (onlyCurrent) {
            for (DataModel.Tenant t : tenantsList) {
                if (t.isCurrent)
                    list.add(t);
            }
        } else {
            list.addAll(tenantsList);
        }
        return list;
    }

    public DataModel.Booking getBookingInfo(String id) {
        DataModel.Booking booking = null;
        for (DataModel.Booking b : bookingsList) {
            if (b.id.equals(id) && (b.closingDate == null || b.closingDate.isEmpty())) {
                booking = b;
                break;
            }
        }

        return booking;
    }

    public DataModel.Booking getBookingInfoForTenant(String id) {
        DataModel.Booking booking = null;
        for (DataModel.Booking b : bookingsList) {
            if (b.tenantId.equals(id)) {
                booking = b;
                break;
            }
        }

        return booking;
    }

    public DataModel.Tenant getTenantInfoForBooking(String id) {
        DataModel.Booking b = getBookingInfo(id);
        DataModel.Tenant tenant = null;
        if (b != null) {
            for (DataModel.Tenant t : tenantsList) {
                if (t.id.equals(b.tenantId)) {
                    tenant = t;
                    break;
                }
            }
        }

        return tenant;
    }

    public int getPendingAmountForBooking(String id) {
        int totalPendingAmount = 0;
        ArrayList<DataModel.Pending> pendings = getPendingEntriesForBooking(id);
        for (DataModel.Pending pendingEntry : pendings) {
            totalPendingAmount += pendingEntry.pendingAmt;
        }

        return totalPendingAmount;
    }

    public ArrayList<DataModel.Pending> getPendingEntriesForBooking(String id) {
        ArrayList<DataModel.Pending> pendingEntries = new ArrayList<DataModel.Pending>();
        for (DataModel.Pending pendingEntry : pendingList) {
            if (pendingEntry.bookingId.equals(id))
                pendingEntries.add(pendingEntry);
        }

        return pendingEntries;
    }

    public ArrayList<DataModel.Tenant> getDependents(String id) {
        ArrayList<DataModel.Tenant> dependentList = new ArrayList<DataModel.Tenant>();
        for (DataModel.Tenant t : tenantsList) {
            if (t.parentId.equals(id)) {
                dependentList.add(t);
            }
        }

        return dependentList;
    }

    public DataModel.Bed getBedInfo(String forBedNumber) {
        DataModel.Bed bed = null;
        for (DataModel.Bed b : roomsList) {
            if (b.bedNumber.equals(forBedNumber)) {
                bed = b;
                break;
            }
        }

        return bed;
    }

    public DataModel.Tenant getTenantInfo(String id) {
        DataModel.Tenant tenant = null;
        for (DataModel.Tenant t : tenantsList) {
            if (t.id.equals(id)) {
                tenant = t;
                break;
            }
        }

        return tenant;
    }

    public int getTotalPendingAmount(DataModel.PendingType type) {
        int pendingAmount = 0;
        for (DataModel.Pending pendingEntry : pendingList) {
            if(pendingEntry.type == type) {
                pendingAmount += pendingEntry.pendingAmt;
            }
        }

        return pendingAmount;
    }

    public ArrayList<DataModel.Receipt> getReceiptsForTenant(String id) {
        ArrayList<DataModel.Receipt> receiptEntries = new ArrayList<DataModel.Receipt>();
        for(DataModel.Receipt receipt: receiptsList) {
            if(receipt.bookingId.equals(id)) {
                receiptEntries.add(receipt);
            }
        }

        return receiptEntries;
    }

    public String  getTotalReceivedAmountForMonth(int month, DataModel.ReceiptType type) {
        Integer receivedAmount = 0;
        for(DataModel.Receipt receipt: receiptsList) {
            Calendar c = Calendar.getInstance();
            try { c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(receipt.date)); }
            catch (ParseException e) { e.printStackTrace(); }
            if (c.get(Calendar.MONTH) == month && receipt.type == type) {
                receivedAmount += Integer.parseInt(receipt.cashAmount) + Integer.parseInt(receipt.onlineAmount);
            }
        }

        return receivedAmount.toString();
    }

    public void addPenaltyToOutstandingPayments(final NetworkDataModulCallback<DataModel.Pending> callback) {
        final int[] callsMade = {0};
        for (DataModel.Pending pendingEntry : pendingList) {
            if(pendingEntry.type == DataModel.PendingType.RENT) {
                callsMade[0]++;
                createPendingEntryForBooking(pendingEntry.bookingId, DataModel.PendingType.PENALTY, "200", Calendar.getInstance().get(Calendar.MONTH) + 1,
                        new NetworkDataModulCallback<DataModel.Pending>() {
                            @Override
                            public void onSuccess(DataModel.Pending obj) {
                                callsMade[0]--;
                                if(callsMade[0] == 0 && callback != null) {
                                    callback.onSuccess(obj);
                                    callback.onResult();
                                }
                            }

                            @Override
                            public void onFailure() {
                                callsMade[0]--;
                                if(callsMade[0] == 0 && callback != null) {
                                    callback.onFailure();
                                    callback.onResult();
                                }
                            }
                        });
            }
        }
    }

    public void createPendingEntryForBooking(String bookingId, DataModel.PendingType type, String amount, int month, final NetworkDataModulCallback<DataModel.Pending> callback) {

        DataModel.Pending pending = new DataModel.Pending(Integer.parseInt(amount), bookingId, null, type, month);
        Call<DataModel.Pending> createPendingEntryCall = pendingService.createPendingEntry (pending);
        Log.i(TAG, "pending Entry Request: " + createPendingEntryCall.request().body().toString());
        createPendingEntryCall.enqueue(new Callback<DataModel.Pending>() {
            @Override
            public void onResponse(Call<DataModel.Pending> call, Response<DataModel.Pending> response) {
                Log.i(TAG, "Pending Entry created Successfully" );

                DataModel.Pending pending = response.body();
                pendingList.add(pending);
                if(callback != null) {
                    callback.onSuccess(pending);
                    callback.onResult();
                }
            }

            @Override
            public void onFailure(Call<DataModel.Pending> call, Throwable t) {
                Log.i(TAG, "Call to CreatePendingEntry Service failed: " + call.request());
                if(callback != null) {
                    callback.onFailure();
                    callback.onResult();
                }
            }
        });
    }

    //TODO: Complete this. Create Receipts Service
    public void createMonthlyPendingEntries(boolean smsPermission, final NetworkDataModulCallback<DataModel.Pending> callback) {
        for (DataModel.Booking booking: bookingsList) {
            int rentAmount = Integer.parseInt(booking.rentAmount);
            for (DataModel.Receipt r: receiptsList) {
                // First check if last month there has been an advance Receipt for this Booking
                Calendar c = Calendar.getInstance();
                try { c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(r.date)); }
                catch (ParseException e) { e.printStackTrace(); }

                if(r.id.equals(booking.id) && r.type == DataModel.ReceiptType.ADVANCE &&
                        c.get(Calendar.MONTH) + 1 == Calendar.getInstance().get(Calendar.MONTH) + 2 ) {
                    rentAmount -= Integer.parseInt(r.cashAmount) + Integer.parseInt(r.onlineAmount);
                }
            }
            createPendingEntryForBooking(booking.id, DataModel.PendingType.RENT, String.valueOf(rentAmount), Calendar.getInstance().get(Calendar.MONTH) + 1, null);
        }
    }

    public void addNewBed(DataModel.Bed bedInfo, final NetworkDataModulCallback<DataModel.Bed> callback) {
        Call<DataModel.Bed> addRoomCall = roomsService.addRoom(bedInfo.bedNumber, bedInfo);

        addRoomCall.enqueue(new Callback<DataModel.Bed>() {
            @Override
            public void onResponse(Call<DataModel.Bed> call, Response<DataModel.Bed> response) {
                Log.i(TAG, "Add Room Successful, Received Response Body: " + response.toString());

                roomsList.add(response.body());
                if(callback != null) {
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DataModel.Bed> call, Throwable t) {
                Log.i(TAG, "Call to Add Room Service failed: " + call.request());
                if(callback != null) {
                    callback.onFailure();
                }
            }
        });
    }

    public void addNewBed(String bedNumber, String rent, String deposit, final NetworkDataModulCallback<DataModel.Bed> callback) {

        DataModel.Bed room = new DataModel.Bed(bedNumber, null, rent, deposit, false);
        addNewBed(room, callback);
    }

    public void createDefaultBeds() {

        /* For Ground Floor */
        addNewBed("000.1", "4250", "4250", null);
        addNewBed("000.2", "4250", "4250", null);
        addNewBed("000.3", "4250", "4250", null);
        addNewBed("000.4", "4250", "4250", null);
        addNewBed("000.5", "4250", "4250", null);
        addNewBed("000.6", "4250", "4250", null);

        for (int floorNo = 100; floorNo <= 600; floorNo += 100) {
            int numOfRooms = 7;

            if (floorNo >= 200) {
                numOfRooms = 8;
            }
            for (int roomNo = 1; roomNo <= numOfRooms; roomNo += 1) {
                Double bedNumber = Double.valueOf(floorNo + roomNo);
                addNewBed(bedNumber.toString(), "8000", "8000", null);
            }
        }
    }


    public void addNewTenant(String name, String mobile, String email, String address, String parentId, boolean isCurrent, final NetworkDataModulCallback<DataModel.Tenant> callback) {
        DataModel.Tenant newTenant = new DataModel.Tenant(null, name, mobile, email, address, isCurrent, parentId);
        Call<DataModel.Tenant> addTenantCall = tenantService.addTenant(newTenant);

        addTenantCall.enqueue(new Callback<DataModel.Tenant>() {
            @Override
            public void onResponse(Call<DataModel.Tenant> call, Response<DataModel.Tenant> response) {
                DataModel.Tenant newTenant = (DataModel.Tenant) response.body();
                tenantsList.add(newTenant);
                if(callback != null) {
                    callback.onSuccess(newTenant);
                }
            }

            @Override
            public void onFailure(Call<DataModel.Tenant> call, Throwable t) {
                Log.i(TAG, "Add new tenant failed");
                if(callback != null) {
                    callback.onFailure();
                }
            }
        });
    }

    public void updateTenant(DataModel.Tenant tenant, NetworkDataModulCallback<DataModel.Tenant> callback) {
        updateTenant(tenant.id, tenant.name, tenant.mobile, "", tenant.email, tenant.address, tenant.isCurrent, tenant.parentId, callback);
    }

    public void updateTenant(String id, String name, String mobile, String mobile2, String email,
                             String address, Boolean isCurrent, @NotNull String parentId,
                             final NetworkDataModulCallback<DataModel.Tenant> callback) {
        DataModel.Tenant newTenant = new DataModel.Tenant(id, name, mobile, email, address, isCurrent, parentId);
        Call<DataModel.Tenant> updateTenantCall = tenantService.updateTenant(id, newTenant);

        updateTenantCall.enqueue(new Callback<DataModel.Tenant>() {
            @Override
            public void onResponse(Call<DataModel.Tenant> call, Response<DataModel.Tenant> response) {
                DataModel.Tenant tenant = (DataModel.Tenant) response.body();
                for (DataModel.Tenant t : tenantsList) {
                    if (t.id.equals(tenant.id)) {
                        tenantsList.set(tenantsList.indexOf(t), tenant);
                        if(callback != null) {
                            callback.onSuccess(tenant);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<DataModel.Tenant> call, Throwable t) {
                Log.i(TAG, "Add new tenant failed");
                if(callback != null) {
                    callback.onFailure();
                }
            }
        });
    }

    public void createNewBooking(final String bedNumber, String tenantId, String rent, String deposit, String admissionDate,
                                 final NetworkDataModulCallback<DataModel.Booking> callback) {
        DataModel.Booking booking = new DataModel.Booking(null, bedNumber, rent, deposit, admissionDate, true, tenantId, null);
        Call<DataModel.Booking> createBookingCall = bookingService.createBooking(booking);

        createBookingCall.enqueue(new Callback<DataModel.Booking>() {
            @Override
            public void onResponse(Call<DataModel.Booking> call, Response<DataModel.Booking> response) {
                Log.i(TAG, "Create Booking Successful");
                DataModel.Booking newBooking = response.body();
                bookingsList.add(newBooking);
                DataModel.Bed bed = getBedInfo(newBooking.bedNumber);
                bed.isOccupied = true;
                bed.bookingId = newBooking.id;
                addNewBed(bed, null);

                DataModel.Tenant tenant = getTenantInfoForBooking(newBooking.id);
                tenant.isCurrent = true;
                updateTenant(tenant, null);

                if(callback != null) {
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DataModel.Booking> call, Throwable t) {
                Log.i(TAG, "Call to Create Booking Service failed: " + call.request());
                if(callback != null) {
                    callback.onFailure();
                }
            }
        });
    }

    public void updateBooking(String id, String newRent, String newDeposit,
                                 final NetworkDataModulCallback<DataModel.Booking> callback) {
        DataModel.Booking booking = getBookingInfo(id);
        booking.rentAmount = newRent;
        booking.depositAmount = newDeposit;

        Call<DataModel.Booking> updateBookingCall = bookingService.updateBooking(id, booking);
        updateBookingCall.enqueue(new Callback<DataModel.Booking>() {
            @Override
            public void onResponse(Call<DataModel.Booking> call, Response<DataModel.Booking> response) {
                Log.i(TAG, "Update Booking Successful");
                DataModel.Booking booking = (DataModel.Booking) response.body();

                for (DataModel.Booking b : bookingsList) {
                    if (b.id.equals(booking.id)) {
                        bookingsList.set(bookingsList.indexOf(b), booking);
                        if (callback != null) {
                            callback.onSuccess(response.body());
                        }
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<DataModel.Booking> call, Throwable t) {
                Log.i(TAG, "Call to UpdateBooking Service failed: " + call.request());
                if(callback != null) {
                    callback.onFailure();
                }
            }
        });
    }

    public void closeBooking(String id, String closeingDate,
                                final NetworkDataModulCallback<DataModel.Booking> callback) {
        DataModel.Booking booking = getBookingInfo(id);
        booking.closingDate = closeingDate;

        Call<DataModel.Booking> updateBookingCall = bookingService.updateBooking(id, booking);
        updateBookingCall.enqueue(new Callback<DataModel.Booking>() {
            @Override
            public void onResponse(Call<DataModel.Booking> call, Response<DataModel.Booking> response) {
                Log.i(TAG, "Close Booking Successful");
                DataModel.Booking booking = (DataModel.Booking) response.body();

                DataModel.Bed bed = getBedInfo(booking.bedNumber);
                bed.isOccupied = false;
                bed.bookingId = null;
                addNewBed(bed, null);

                DataModel.Tenant tenant = getTenantInfoForBooking(booking.id);
                tenant.isCurrent = false;
                updateTenant(tenant, null);

                int index = -1;
                for (DataModel.Booking b : bookingsList) {
                    if (b.id.equals(booking.id)) {
                        index = bookingsList.indexOf(b);
                        break;
                    }
                }
                if(index != -1) {
                    bookingsList.remove(index);
                }

                if (callback != null) {
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DataModel.Booking> call, Throwable t) {
                Log.i(TAG, "Call to UpdateBooking Service for Close failed: " + call.request());
                if(callback != null) {
                    callback.onFailure();
                }
            }
        });
    }

    public void splitRoom(String roomNumber, int numRooms, String rent, String deposit) {
        roomNumber = roomNumber.split("\\.")[0];
        for (int i = 1; i< numRooms; i++) {
            addNewBed(roomNumber + "." + String.valueOf(i), rent, deposit, null);
        }
    }
}

class Room {

}