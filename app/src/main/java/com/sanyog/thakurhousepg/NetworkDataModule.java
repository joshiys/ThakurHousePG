package com.sanyog.thakurhousepg;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import static com.sanyog.thakurhousepg.Constants.THAKURHOUSE_DATE_FORMAT;
import static com.sanyog.thakurhousepg.Constants.INITIAL_PING_DELAY;
import static com.sanyog.thakurhousepg.Constants.PING_PERIOD;
import static com.sanyog.thakurhousepg.Constants.THAKURHOUSEPG_BASE_URL;


//region Service Interfaces

interface RoomsService {
    @GET("rooms")
    Call<List<DataModel.Bed>> listRooms();

    @POST("rooms/{number}")
    Call<DataModel.Bed> updateRoom(@Path("number") String roomNumber, @Body DataModel.Bed room);

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

    @DELETE("pending/{id}")
    Call<Long> deletePendingEntry(@Path("id") int id);
}

interface ReceiptService {
    @GET("receipts")
    Call<List<DataModel.Receipt>> getAllReceipts();

    @POST("receipts/{id}")
    Call<DataModel.Receipt> updateReceipt(@Path("id") String id, @Body DataModel.Receipt receiptInfo);

    @PUT("/receipts")
    Call<DataModel.Receipt> createReceipt(@Body DataModel.Receipt receipt);
}

interface SettingsService {
    @GET("/Settings/settingEntries")
    Call<DataModel.Settings> getSettingEntriesUpdatedForMonth();

    @GET("/Settings/getNextSequence")
    Call<Long> getNextSequence();

    @POST("/Settings/pendingEntriesMonth/{month}")
    Call<Long> setPendingEntriesUpdatedForMonth(@Path("month") Integer month);

    @POST("/Settings/penaltyAddedForMonth/{month}")
    Call<Long> setPenaltyAddedForMonth(@Path("month") Integer month);
}
//endregion

interface NetworkDataModuleCallback<T> {
    void onSuccess(T obj);
    void onFailure();
    default void onResult() {
        System.out.println("Default OnResult implementation");
    }
}

public class NetworkDataModule {
    //region Variables
    private static final String TAG = "NetworkDataModule";
    private static NetworkDataModule _instance = null;

    private HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS);
    private Retrofit retrofit;

    private ArrayList<DataModel.Bed> roomsList = new ArrayList<DataModel.Bed>();
    private ArrayList<DataModel.Tenant> tenantsList = new ArrayList<DataModel.Tenant>();
    private ArrayList<DataModel.Booking> bookingsList = new ArrayList<DataModel.Booking>();
    private ArrayList<DataModel.Pending> pendingList = new ArrayList<DataModel.Pending>();
    private ArrayList<DataModel.Receipt> receiptsList = new ArrayList<DataModel.Receipt>();

    private RoomsService roomsService;
    private TenantService tenantService;
    private BookingService bookingService;
    private PendingService pendingService;
    private ReceiptService receiptsService;
    private SettingsService settingsService;

    private final ScheduledExecutorService pinger = Executors.newScheduledThreadPool(1);
    private int pendingEntriesUpdatedForMonth;
    private int penaltyEntriesAddedForMonth;
    private long sequenceNumber;

    //endregion

    public static NetworkDataModule getInstance() {
        if (_instance == null) {
            _instance = new NetworkDataModule(THAKURHOUSEPG_BASE_URL);
        }

        return _instance;
    }

    public static NetworkDataModule getInstance(String baseURL) {
        if (_instance == null) {
            _instance = new NetworkDataModule(baseURL);
        }

        return _instance;
    }


    private NetworkDataModule(String baseURL) {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);
        Gson gson = new GsonBuilder()
                .setDateFormat(THAKURHOUSE_DATE_FORMAT)
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        roomsService = retrofit.create(RoomsService.class);
        tenantService = retrofit.create(TenantService.class);
        bookingService = retrofit.create(BookingService.class);
        pendingService = retrofit.create(PendingService.class);
        receiptsService = retrofit.create(ReceiptService.class);
        settingsService = retrofit.create(SettingsService.class);

        pendingEntriesUpdatedForMonth = 0;
        penaltyEntriesAddedForMonth = 0;

        pinger.scheduleAtFixedRate(() -> ping(), INITIAL_PING_DELAY, PING_PERIOD, TimeUnit.SECONDS);
    }

    public void loadData(NetworkDataModuleCallback callback) {
        NetworkMergeCallback waitingCallback = new NetworkMergeCallback(6, callback, null);
        retrieveRoomsList(waitingCallback);
        retrieveTenantsList(waitingCallback);
        retrieveBookings(waitingCallback);
        retrievePendingEntries(waitingCallback);
        retrieveReceipts(waitingCallback);
        retrieveSettings(waitingCallback);
    }

    private void ping() {
        Call<Long> nextSequence = settingsService.getNextSequence();
        nextSequence.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                Long newSeqNumber = response.body();
                if (sequenceNumber == 0) {
                    sequenceNumber = newSeqNumber;
                } else if (sequenceNumber < newSeqNumber) {
                    sequenceNumber = newSeqNumber;
                    loadData(null);
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
            }
        });
    }

    //region Retrieve Initial resources
    private void retrieveRoomsList(NetworkDataModuleCallback callback) {
        Call<List<DataModel.Bed>> roomsCall = roomsService.listRooms();

        roomsCall.enqueue(new Callback<List<DataModel.Bed>>() {
            @Override
            public void onResponse(Call<List<DataModel.Bed>> call, Response<List<DataModel.Bed>> response) {
                roomsList.clear();
                roomsList.addAll(response.body());
                roomsList.sort((DataModel.Bed o1, DataModel.Bed o2) -> o1.bedNumber.compareTo(o2.bedNumber));
                if (callback != null)
                    callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<List<DataModel.Bed>> call, Throwable t) {
                Log.i(TAG, "Call to Room Service failed");
                if (callback != null)
                    callback.onFailure();
            }
        });
    }

    private void retrieveBookings(NetworkDataModuleCallback callback) {
        Call<List<DataModel.Booking>> roomsCall = bookingService.getAllBookings();

        roomsCall.enqueue(new Callback<List<DataModel.Booking>>() {
            @Override
            public void onResponse(Call<List<DataModel.Booking>> call, Response<List<DataModel.Booking>> response) {
                bookingsList.clear();
                bookingsList.addAll(response.body());
                bookingsList.sort((DataModel.Booking o1, DataModel.Booking o2) -> o2.id.compareTo(o1.id));

                if (callback != null)
                    callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<List<DataModel.Booking>> call, Throwable t) {
                Log.i(TAG, "Call to Bookings Service failed");
                if (callback != null)
                    callback.onFailure();
            }
        });
    }

    private void retrieveTenantsList(NetworkDataModuleCallback callback) {
        Call<List<DataModel.Tenant>> tenantsCall = tenantService.getAllTenants();

        tenantsCall.enqueue(new Callback<List<DataModel.Tenant>>() {
            @Override
            public void onResponse(Call<List<DataModel.Tenant>> call, Response<List<DataModel.Tenant>> response) {
                tenantsList.clear();
                tenantsList.addAll(response.body());
                tenantsList.sort((DataModel.Tenant o1, DataModel.Tenant o2) -> o2.id.compareTo(o1.id));

                if (callback != null)
                    callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<List<DataModel.Tenant>> call, Throwable t) {
                Log.i(TAG, "Call to Tenant Service failed");
                if (callback != null)
                    callback.onFailure();
            }
        });
    }

    private void retrievePendingEntries(NetworkDataModuleCallback callback) {
        Call<List<DataModel.Pending>> pendingCall = pendingService.getAllPendingEntries();

        pendingCall.enqueue(new Callback<List<DataModel.Pending>>() {
            @Override
            public void onResponse(Call<List<DataModel.Pending>> call, Response<List<DataModel.Pending>> response) {
                pendingList.clear();
                pendingList.addAll(response.body());

                if (callback != null)
                    callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<List<DataModel.Pending>> call, Throwable t) {
                Log.i(TAG, "Call to Pending Service failed");
                if (callback != null)
                    callback.onFailure();
            }
        });
    }

    private void retrieveReceipts(NetworkDataModuleCallback callback) {
        Call<List<DataModel.Receipt>> getReceiptsCall = receiptsService.getAllReceipts();

        getReceiptsCall.enqueue(new Callback<List<DataModel.Receipt>>() {
            @Override
            public void onResponse(Call<List<DataModel.Receipt>> call, Response<List<DataModel.Receipt>> response) {
                receiptsList.clear();
                receiptsList.addAll(response.body());
                receiptsList.sort((DataModel.Receipt o1, DataModel.Receipt o2) -> o2.id.compareTo(o1.id));
                if (callback != null)
                    callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<List<DataModel.Receipt>> call, Throwable t) {
                Log.i(TAG, "Call to Receipt Service failed" );
                if (callback != null)
                    callback.onFailure();
            }
        });
    }

    private void retrieveSettings(NetworkDataModuleCallback callback) {
        Call<DataModel.Settings> getSettings = settingsService.getSettingEntriesUpdatedForMonth();

        getSettings.enqueue(new Callback<DataModel.Settings>() {
            @Override
            public void onResponse(Call<DataModel.Settings> call, Response<DataModel.Settings> response) {
                DataModel.Settings s = response.body();
                pendingEntriesUpdatedForMonth = s.pendingEntriesUpdatedForMonth;
                penaltyEntriesAddedForMonth = s.penaltyAddedForMonth;
                if (callback != null)
                    callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<DataModel.Settings> call, Throwable t) {
                if (callback != null)
                    callback.onFailure();
            }
        });
    }

    //endregion


    public Integer getPendingEntriesUpdatedForMonth() {
        return pendingEntriesUpdatedForMonth;
    }

    public Integer getPenaltyAddedForMonth() {
        return penaltyEntriesAddedForMonth;
    }

    public void setPendingEntriesUpdatedForMonth(Integer month) {
        Call<Long> setSettings = settingsService.setPendingEntriesUpdatedForMonth(month);
        setSettings.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                sequenceNumber = response.body();
                pendingEntriesUpdatedForMonth = month;
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
            }
        });
    }

    public void setPenaltyAddedForMonth(Integer month) {
        Call<Long> setSettings = settingsService.setPenaltyAddedForMonth(month);
        setSettings.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                sequenceNumber = response.body();
                penaltyEntriesAddedForMonth = month;
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
            }
        });
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

    public ArrayList<DataModel.Booking> getCurrentBookings() {
        ArrayList<DataModel.Booking> currentBookings = new ArrayList<>();
        for (DataModel.Booking b: bookingsList) {
            if(b.closingDate == null)
                currentBookings.add(b);
        }

        return currentBookings;
    }

    public ArrayList<DataModel.Booking> getAllBookingInfo() {
        ArrayList<DataModel.Booking> bookingList = new ArrayList<>();
        bookingList.addAll(bookingsList);

        return bookingList;
    }

    public DataModel.Booking getBookingInfo(String id) {
        DataModel.Booking booking = null;
        for (DataModel.Booking b : bookingsList) {
            if (b.id.equals(id)) { //&& (b.closingDate == null || b.closingDate.isEmpty())
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

    public ArrayList<DataModel.Pending> getAllPendingEntries() {
        ArrayList<DataModel.Pending> pendingArrayList = new ArrayList<>();
        pendingArrayList.addAll(pendingList);

        return pendingArrayList;
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

    public DataModel.Pending getPendingEntryByID(String id) {
        DataModel.Pending pending = null;
        for (DataModel.Pending pendingEntry : pendingList) {
            if (pendingEntry.id.equals(id))
                pending = pendingEntry;
        }

        return pending;
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
        DataModel.Booking booking = getBookingInfoForTenant(id);
        for(DataModel.Receipt receipt: receiptsList) {
            if(receipt.bookingId.equals(booking.id)) {
                receiptEntries.add(receipt);
            }
        }

        return receiptEntries;
    }

    public ArrayList<DataModel.Receipt> getAllReceipts(int month) {
        ArrayList<DataModel.Receipt> receiptEntries = new ArrayList<DataModel.Receipt>();
        for(DataModel.Receipt receipt: receiptsList) {
            if (getMonth(receipt.date) == month) {
                receiptEntries.add(receipt);
            }
        }

        return receiptEntries;
    }

    public String getTotalCashReceipts(int month, DataModel.ReceiptType receiptType) {
        Integer receivedAmount = 0;
        for(DataModel.Receipt receipt: receiptsList) {
            if (getMonth(receipt.date) == month && receipt.type == receiptType) {
                receivedAmount += Integer.parseInt(receipt.cashAmount);
            }
        }

        return receivedAmount.toString();
    }

    public String  getTotalReceivedAmountForMonth(int month, DataModel.ReceiptType type) {
        Integer receivedAmount = 0;
        for(DataModel.Receipt receipt: receiptsList) {
            if (getMonth(receipt.date) == month && receipt.type == type) {
                receivedAmount += Integer.parseInt(receipt.cashAmount) + Integer.parseInt(receipt.onlineAmount);
            }
        }

        return receivedAmount.toString();
    }

    public String getTotalExpectedRent() {
        int totalRent = 0;
        for (DataModel.Booking b: bookingsList) {
            if(b.closingDate == null || getMonth(b.closingDate) == (Calendar.getInstance().get(Calendar.MONTH) + 1)) {
                totalRent += Integer.parseInt(b.rentAmount);
            }
        }

        return String.valueOf(totalRent);
    }

    public void addPenaltyToOutstandingPayments(final NetworkDataModuleCallback<DataModel.Pending> callback) {
        final int[] callsMade = {0};
        for (DataModel.Pending pendingEntry : pendingList) {
            if(pendingEntry.type == DataModel.PendingType.RENT) {
                callsMade[0]++;
                createPendingEntryForBooking(pendingEntry.bookingId, DataModel.PendingType.PENALTY, "200", Calendar.getInstance().get(Calendar.MONTH) + 1,
                        new NetworkDataModuleCallback<DataModel.Pending>() {
                            @Override
                            public void onSuccess(DataModel.Pending obj) {
                                callsMade[0]--;
                                if(callsMade[0] == 0 && callback != null) {
                                    sequenceNumber = obj.sequenceNumber;
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
                DataModel.Tenant tenant = getTenantInfoForBooking(pendingEntry.bookingId);
                if(!tenant.mobile.isEmpty()) {
                    SMSManagement smsManagement = SMSManagement.getInstance();

                    smsManagement.sendSMS(tenant.mobile,
                            smsManagement.getSMSMessage(pendingEntry.bookingId,
                                    tenant,
                                    0,
                                    SMSManagement.SMS_TYPE.PENALTY_GENERATED)
                    );
                }
            }
        }
    }

    public void createPendingEntryForBooking(String bookingId, DataModel.PendingType type, String amount, int month, final NetworkDataModuleCallback<DataModel.Pending> callback) {

        DataModel.Pending pending = new DataModel.Pending(null, Integer.parseInt(amount), bookingId, null, type, month);
        Call<DataModel.Pending> createPendingEntryCall = pendingService.createPendingEntry (pending);
        Log.i(TAG, "pending Entry Request: " + createPendingEntryCall.request().body().toString());
        createPendingEntryCall.enqueue(new Callback<DataModel.Pending>() {
            @Override
            public void onResponse(Call<DataModel.Pending> call, Response<DataModel.Pending> response) {
                Log.i(TAG, "Pending Entry created Successfully" );

                DataModel.Pending pending = response.body();
                pendingList.add(pending);
                if(callback != null) {
                    sequenceNumber = pending.sequenceNumber;
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
    public void createMonthlyPendingEntries(boolean smsPermission, final NetworkDataModuleCallback<DataModel.Pending> callback) {
        for (DataModel.Booking booking: getCurrentBookings()) {
            int rentAmount = Integer.parseInt(booking.rentAmount);
            for (DataModel.Receipt r: receiptsList) {
                // Also check if last month there has been an advance Receipt for this Booking
                if(r.id.equals(booking.id) && r.type == DataModel.ReceiptType.ADVANCE &&
                    getMonth(r.date) + 1 == Calendar.getInstance().get(Calendar.MONTH) + 2 ) {
                    rentAmount -= Integer.parseInt(r.cashAmount) + Integer.parseInt(r.onlineAmount);
                }
            }
            createPendingEntryForBooking(booking.id, DataModel.PendingType.RENT, String.valueOf(rentAmount), Calendar.getInstance().get(Calendar.MONTH) + 1, callback);

            if(smsPermission) {
                DataModel.Tenant tenant = getTenantInfoForBooking(booking.id);
                if (tenant != null && !tenant.mobile.isEmpty()) {
                    SMSManagement smsManagement = SMSManagement.getInstance();

                    smsManagement.sendSMS(tenant.mobile,
                            SMSManagement.getInstance().getSMSMessage(booking.id,
                                    tenant,
                                    0,
                                    SMSManagement.SMS_TYPE.MONTHLY_RENT)
                    );
                }
            }
        }
    }

    public void updatePendingEntry(String id, String amount, NetworkDataModuleCallback<? super DataModel.Pending> callback) {
        DataModel.Pending pending = getPendingEntryByID(id);
        int totalPendingAmount = pending.pendingAmt;

        totalPendingAmount -= Integer.parseInt(amount);
        if (totalPendingAmount <= 0) {
            Call <Long> pendingCall = pendingService.deletePendingEntry(Integer.parseInt(pending.id));
            pendingCall.enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    if (callback != null) {
                        pendingList.remove(pending);
                        sequenceNumber = response.body();
                        callback.onSuccess(null);
                        callback.onResult();
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    if (callback != null) {
                        callback.onFailure();
                        callback.onResult();
                    }
                }
            });
        } else {
            Call <DataModel.Pending> pendingCall;
            pending.pendingAmt = totalPendingAmount;
            pendingCall = pendingService.updatePendingEntry(pending.bookingId, pending);
            pendingCall.enqueue(new Callback<DataModel.Pending>() {
                @Override
                public void onResponse(Call<DataModel.Pending> call, Response<DataModel.Pending> response) {
                    if (callback != null) {
                        DataModel.Pending updatedPendingEntry = response.body();
                        pendingList.set(pendingList.indexOf(pending), updatedPendingEntry);
                        sequenceNumber = updatedPendingEntry.sequenceNumber;
                        callback.onSuccess(updatedPendingEntry);
                        callback.onResult();
                    }
                }

                @Override
                public void onFailure(Call<DataModel.Pending> call, Throwable t) {
                    if (callback != null) {
                        callback.onFailure();
                        callback.onResult();
                    }
                }
            });
        }
    }

    public void updatePendingEntryForBooking(String id, DataModel.PendingType type, String amount, NetworkDataModuleCallback<? super DataModel.Pending> callback) {

        for (DataModel.Pending p: pendingList) {
            if(p.bookingId.equals(id) && p.type == type) {
                updatePendingEntry(p.id, amount, callback);
                break;
            }
        }
    }

    private void deletePendingEntriesForBooking(String id, NetworkDataModuleCallback<? super DataModel.Pending> callback) {
        for (DataModel.Pending p: pendingList) {
            if(p.bookingId.equals(id)) {
                updatePendingEntry(p.id, String.valueOf(p.pendingAmt), callback);
            }
        }
    }

    private void addNewBed(DataModel.Bed bedInfo, final NetworkDataModuleCallback<DataModel.Bed> callback) {
        Call<DataModel.Bed> addRoomCall = roomsService.addRoom(bedInfo.bedNumber, bedInfo);

        addRoomCall.enqueue(new Callback<DataModel.Bed>() {
            @Override
            public void onResponse(Call<DataModel.Bed> call, Response<DataModel.Bed> response) {
                Log.i(TAG, "Add Room Successful, Received Response Body: " + response.toString());

                DataModel.Bed newBed = response.body();

                int index = Collections.binarySearch(roomsList, newBed, Comparator.comparing(DataModel.Bed::getBedNumber));
                if (index < 0) {
                    index = -index - 1;
                }

                roomsList.add(index, newBed);

                if(callback != null) {
                    sequenceNumber = newBed.sequenceNumber;
                    callback.onSuccess(newBed);
                    callback.onResult();
                }
            }

            @Override
            public void onFailure(Call<DataModel.Bed> call, Throwable t) {
                Log.i(TAG, "Call to Add Room Service failed: " + call.request());
                if(callback != null) {
                    callback.onFailure();
                    callback.onResult();
                }
            }
        });
    }

    private void addNewBed(String bedNumber, String rent, String deposit, final NetworkDataModuleCallback<DataModel.Bed> callback) {

        DataModel.Bed room = new DataModel.Bed(String.valueOf(sequenceNumber), bedNumber, null, rent, deposit, false);
        addNewBed(room, callback);
    }

    private void updateBed(DataModel.Bed bedInfo, final NetworkDataModuleCallback<? super DataModel.Bed> callback) {
        Call<DataModel.Bed> updateBedCall = roomsService.updateRoom(bedInfo.bedNumber, bedInfo);
        updateBedCall.enqueue(new Callback<DataModel.Bed>() {
            @Override
            public void onResponse(Call<DataModel.Bed> call, Response<DataModel.Bed> response) {
                DataModel.Bed newBed = response.body();
                for (DataModel.Bed bed: roomsList) {
                    if(bed.bedNumber.equals(newBed.bedNumber)) {
                        roomsList.set(roomsList.indexOf(bed), newBed);
                        break;
                    }
                }
                if(callback != null) {
                    sequenceNumber = newBed.sequenceNumber;
                    callback.onSuccess(newBed);
                    callback.onResult();
                }
            }

            @Override
            public void onFailure(Call<DataModel.Bed> call, Throwable t) {
                if(callback != null) {
                    callback.onFailure();
                    callback.onResult();
                }

            }
        });
    }

    public void addNewTenant(String name, String mobile, String email, String address, String parentId, boolean isCurrent, final NetworkDataModuleCallback<DataModel.Tenant> callback) {
        DataModel.Tenant newTenant = new DataModel.Tenant(null, name, mobile, email, address, isCurrent, parentId);
        Call<DataModel.Tenant> addTenantCall = tenantService.addTenant(newTenant);

        addTenantCall.enqueue(new Callback<DataModel.Tenant>() {
            @Override
            public void onResponse(Call<DataModel.Tenant> call, Response<DataModel.Tenant> response) {
                DataModel.Tenant newTenant = (DataModel.Tenant) response.body();
                tenantsList.add(0, newTenant);
                if(callback != null) {
                    sequenceNumber = newTenant.sequenceNumber;
                    callback.onSuccess(newTenant);
                    callback.onResult();
                }
            }

            @Override
            public void onFailure(Call<DataModel.Tenant> call, Throwable t) {
                Log.i(TAG, "Add new tenant failed");
                if(callback != null) {
                    callback.onFailure();
                    callback.onResult();
                }
            }
        });
    }

    public void updateTenant(DataModel.Tenant tenant, NetworkDataModuleCallback<? super DataModel.Tenant> callback) {
        updateTenant(tenant.id, tenant.name, tenant.mobile, "", tenant.email, tenant.address, tenant.isCurrent, tenant.parentId, callback);
    }

    public void updateTenant(String id, String name, String mobile, String mobile2, String email,
                             String address, Boolean isCurrent, @NotNull String parentId,
                             final NetworkDataModuleCallback<? super DataModel.Tenant> callback) {
        DataModel.Tenant newTenant = getTenantInfo(id); //new DataModel.Tenant(id, name, mobile, email, address, isCurrent, parentId);
        if(!name.isEmpty()) newTenant.name = name;
        if(!email.isEmpty()) newTenant.email = email;
        if(!mobile.isEmpty()) newTenant.mobile = mobile;
        if(!address.isEmpty()) newTenant.address = address;
        newTenant.isCurrent = isCurrent != null ? isCurrent : false;
        if(!parentId.isEmpty()) newTenant.parentId = parentId;

        Call<DataModel.Tenant> updateTenantCall = tenantService.updateTenant(id, newTenant);

        updateTenantCall.enqueue(new Callback<DataModel.Tenant>() {
            @Override
            public void onResponse(Call<DataModel.Tenant> call, Response<DataModel.Tenant> response) {
                DataModel.Tenant tenant = response.body();
                for (DataModel.Tenant t : tenantsList) {
                    if (t.id.equals(tenant.id)) {
                        tenantsList.set(tenantsList.indexOf(t), tenant);
                        if(callback != null) {
                            sequenceNumber = tenant.sequenceNumber;
                            callback.onSuccess(tenant);
                            callback.onResult();
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
                    callback.onResult();
                }
            }
        });
    }

    public void createNewBooking(final String bedNumber, String tenantId, String rent, String deposit, String admissionDate,
                                 final NetworkDataModuleCallback<? super DataModel.DataModelClass> callback) {

        DataModel.Booking booking = new DataModel.Booking(null, bedNumber, rent, deposit, admissionDate, true, tenantId, null);
        Call<DataModel.Booking> createBookingCall = bookingService.createBooking(booking);

        createBookingCall.enqueue(new Callback<DataModel.Booking>() {
            @Override
            public void onResponse(Call<DataModel.Booking> call, Response<DataModel.Booking> response) {
                Log.i(TAG, "Create Booking Successful");
                DataModel.Booking newBooking = response.body();
                bookingsList.add(0, newBooking);

                NetworkMergeCallback waitingCallback = new NetworkMergeCallback(2, callback, newBooking);

                DataModel.Bed bed = getBedInfo(newBooking.bedNumber);
                bed.isOccupied = true;
                bed.bookingId = newBooking.id;
                updateBed(bed, waitingCallback);

                DataModel.Tenant tenant = getTenantInfoForBooking(newBooking.id);
                tenant.isCurrent = true;
                updateTenant(tenant, waitingCallback);
            }

            @Override
            public void onFailure(Call<DataModel.Booking> call, Throwable t) {
                Log.i(TAG, "Call to Create Booking Service failed: " + call.request());
                if(callback != null) {
                    callback.onFailure();
                    callback.onResult();
                }
            }
        });
    }

    public void updateBooking(String id, String newRent, String newDeposit,
                                 final NetworkDataModuleCallback<? super DataModel.DataModelClass> callback) {
        DataModel.Booking booking = getBookingInfo(id);
        booking.rentAmount = newRent;
        booking.depositAmount = newDeposit;

        Call<DataModel.Booking> updateBookingCall = bookingService.updateBooking(id, booking);
        updateBookingCall.enqueue(new Callback<DataModel.Booking>() {
            @Override
            public void onResponse(Call<DataModel.Booking> call, Response<DataModel.Booking> response) {
                Log.i(TAG, "Update Booking Successful");
                DataModel.Booking booking = (DataModel.Booking) response.body();
                NetworkMergeCallback waitingCallback = new NetworkMergeCallback(0, callback, booking);


                for (DataModel.Booking b : getCurrentBookings()) {
                    if (b.id.equals(booking.id)) {
                        bookingsList.set(bookingsList.indexOf(b), booking);
                    }
                }

                for(DataModel.Pending p: getPendingEntriesForBooking (booking.id)) {
                    String amt = "";
                    if (Integer.parseInt(booking.rentAmount) != p.pendingAmt && p.type == DataModel.PendingType.RENT) {
                        amt = booking.rentAmount;
                    }
                    if (Integer.parseInt(booking.depositAmount) != p.pendingAmt && p.type == DataModel.PendingType.DEPOSIT) {
                        amt = booking.depositAmount;
                    }

                    if (!amt.isEmpty()) {
                        waitingCallback.expectedCallBackCount += 1;
                        updatePendingEntryForBooking(booking.id, p.type, amt, waitingCallback);
                    }
                }
            }

            @Override
            public void onFailure(Call<DataModel.Booking> call, Throwable t) {
                Log.i(TAG, "Call to UpdateBooking Service failed: " + call.request());
                if(callback != null) {
                    callback.onFailure();
                    callback.onResult();
                }
            }
        });
    }


    public void closeBooking(String id, String closeingDate, boolean cancelBooking,
                                final NetworkDataModuleCallback<? super DataModel.DataModelClass> callback) {
        DataModel.Booking booking = getBookingInfo(id);
        booking.closingDate = closeingDate;

        Call<DataModel.Booking> updateBookingCall = bookingService.updateBooking(id, booking);
        updateBookingCall.enqueue(new Callback<DataModel.Booking>() {
            @Override
            public void onResponse(Call<DataModel.Booking> call, Response<DataModel.Booking> response) {
                Log.i(TAG, "Close Booking Successful");
                DataModel.Booking newBooking = response.body();

                for (DataModel.Booking b : getCurrentBookings()) {
                    if (b.id.equals(newBooking.id)) {
                        bookingsList.set(bookingsList.indexOf(b), newBooking);
                        break;
                    }
                }

                NetworkMergeCallback waitingCallback = new NetworkMergeCallback(2, callback, booking);

                DataModel.Bed bed = getBedInfo(newBooking.bedNumber);
                bed.isOccupied = false;
                bed.bookingId = null;
                updateBed(bed, waitingCallback);

                DataModel.Tenant tenant = getTenantInfoForBooking(newBooking.id);
                tenant.isCurrent = false;
                tenant.parentId = "0";
                updateTenant(tenant, waitingCallback);

                waitingCallback.expectedCallBackCount += getDependents(tenant.id).size();

                for (DataModel.Tenant t: getDependents(tenant.id)){
                    t.isCurrent = false;
                    t.parentId = "0";
                    updateTenant(t, waitingCallback);
                }

                waitingCallback.expectedCallBackCount += getPendingEntriesForBooking(newBooking.id).size();
                if(cancelBooking) {
                    deletePendingEntriesForBooking(newBooking.id, waitingCallback);
                }
            }

            @Override
            public void onFailure(Call<DataModel.Booking> call, Throwable t) {
                Log.i(TAG, "Call to UpdateBooking Service for Close failed: " + call.request());
                if(callback != null) {
                    callback.onFailure();
                    callback.onResult();
                }
            }
        });
    }

    public void createReceiptForPendingEntry(String id, String onlineAmount, String cashAmount, boolean penaltyWaiveOff, NetworkDataModuleCallback<? super DataModel.DataModelClass> callback) {
        DataModel.Pending pendingEntry = getPendingEntryByID(id);

        createReceipt(DataModel.ReceiptType.values()[pendingEntry.type.getIntValue()], pendingEntry.bookingId, onlineAmount, cashAmount, penaltyWaiveOff,
                new NetworkDataModuleCallback<DataModel.Receipt>() {
                    @Override
                    public void onSuccess(DataModel.Receipt newReceipt) {
                        NetworkMergeCallback waitingCallback = new NetworkMergeCallback(1, callback, newReceipt);

                        updatePendingEntry(pendingEntry.id, String.valueOf(pendingEntry.pendingAmt), waitingCallback);
                    }

                    @Override
                    public void onFailure() {
                        if(callback != null) {
                            callback.onFailure();
                            callback.onResult();
                        }
                    }
        });
    }

    public void createReceipt(DataModel.ReceiptType type, String bookingId, String onlineAmount, String cashAmount, boolean penaltyWaiveOff, NetworkDataModuleCallback<DataModel.Receipt> callback) {
        if (bookingId==null) throw new AssertionError("Object cannot be null");
        DataModel.Receipt newReceipt = new DataModel.Receipt(null, bookingId, onlineAmount, cashAmount, penaltyWaiveOff, new SimpleDateFormat(THAKURHOUSE_DATE_FORMAT).format(new Date()).toString(), type);
        Call<DataModel.Receipt> createReceiptCall = receiptsService.createReceipt(newReceipt);

        createReceiptCall.enqueue(new Callback<DataModel.Receipt>() {
            @Override
            public void onResponse(Call<DataModel.Receipt> call, Response<DataModel.Receipt> response) {
                Log.i(TAG, "Create Receipt Successful");
                DataModel.Receipt receipt = response.body();
                receiptsList.add(0, receipt);
                if(callback != null) {
                    sequenceNumber = receipt.sequenceNumber;
                    callback.onSuccess(receipt);
                    callback.onResult();
                }
            }

            @Override
            public void onFailure(Call<DataModel.Receipt> call, Throwable t) {
                if(callback != null) {
                    callback.onFailure();
                    callback.onResult();
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

    private int getMonth(String fromDate) {
        Calendar c = Calendar.getInstance();
        try { c.setTime(new SimpleDateFormat(THAKURHOUSE_DATE_FORMAT).parse(fromDate)); }
        catch (ParseException e) { e.printStackTrace(); }

        Log.i(TAG, "getMonth : " + c.get(Calendar.MONTH));
        return c.get(Calendar.MONTH) + 1;
    }

    private class NetworkMergeCallback implements NetworkDataModuleCallback<DataModel.DataModelClass> {

        public int expectedCallBackCount= 0;
        private int currentCount = 0;
        private NetworkDataModuleCallback<? super DataModel.DataModelClass> originalCallBack = null;
        DataModel.DataModelClass passbackObject = null;

        public NetworkMergeCallback(int expectedCallBackCount, NetworkDataModuleCallback<? super DataModel.DataModelClass> originalCallBack, DataModel.DataModelClass passbackObject) {
            this.expectedCallBackCount = expectedCallBackCount;
            this.originalCallBack = originalCallBack;
            this.passbackObject = passbackObject;
        }

        @Override
        public void onSuccess(DataModel.DataModelClass obj) {
            currentCount ++;
            if(currentCount >= expectedCallBackCount) {
                if (originalCallBack != null) {
                    if (obj != null)
                        sequenceNumber = obj.sequenceNumber;

                    originalCallBack.onSuccess(passbackObject != null?passbackObject:obj);
                }
            }
        }
        @Override
        public void onFailure() {
            currentCount ++;
            if(currentCount >= expectedCallBackCount) {
                if (originalCallBack != null) {
                    originalCallBack.onFailure();
                }
            }
        }

        @Override
        public void onResult() {
            System.out.println("Default OnResult implementation");
            if(currentCount >= expectedCallBackCount) {
                if (originalCallBack != null) {
                    originalCallBack.onResult();
                }
            }
        }
    }
}
