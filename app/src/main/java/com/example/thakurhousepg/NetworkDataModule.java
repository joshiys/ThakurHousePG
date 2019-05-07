package com.example.thakurhousepg;


import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    Call<ResponseBody> deletePendingEntry(@Path("id") int id);
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
    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS);
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
                roomsList.sort(new Comparator<DataModel.Bed>() {
                    @Override
                    public int compare(DataModel.Bed o1, DataModel.Bed o2) {
                        return o1.bedNumber.compareTo(o2.bedNumber);
                    }
                });

                invokeInitialDataFetchComplettionCallback(true);
            }

            @Override
            public void onFailure(Call<List<DataModel.Bed>> call, Throwable t) {
                Log.i(TAG, "Call to Room Service failed");
                invokeInitialDataFetchComplettionCallback(false);
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
                bookingsList.sort(new Comparator<DataModel.Booking>() {
                    @Override
                    public int compare(DataModel.Booking o1, DataModel.Booking o2) {
                        return o2.id.compareTo(o1.id);
                    }
                });

                invokeInitialDataFetchComplettionCallback(true);
            }

            @Override
            public void onFailure(Call<List<DataModel.Booking>> call, Throwable t) {
                Log.i(TAG, "Call to Bookings Service failed");
                invokeInitialDataFetchComplettionCallback(false);
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
                tenantsList.sort(new Comparator<DataModel.Tenant>() {
                    @Override
                    public int compare(DataModel.Tenant o1, DataModel.Tenant o2) {
                        return o2.id.compareTo(o1.id);
                    }
                });

                invokeInitialDataFetchComplettionCallback(true);
            }

            @Override
            public void onFailure(Call<List<DataModel.Tenant>> call, Throwable t) {
                Log.i(TAG, "Call to Tenant Service failed");
                invokeInitialDataFetchComplettionCallback(false);
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

                invokeInitialDataFetchComplettionCallback(true);
            }

            @Override
            public void onFailure(Call<List<DataModel.Pending>> call, Throwable t) {
                Log.i(TAG, "Call to Pending Service failed");
                invokeInitialDataFetchComplettionCallback(false);
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
                receiptsList.sort(new Comparator<DataModel.Receipt>() {
                    @Override
                    public int compare(DataModel.Receipt o1, DataModel.Receipt o2) {
                        return o2.id.compareTo(o1.id);
                    }
                });

                invokeInitialDataFetchComplettionCallback(true);
            }

            @Override
            public void onFailure(Call<List<DataModel.Receipt>> call, Throwable t) {
                Log.i(TAG, "Call to Receipt Service failed" );
                invokeInitialDataFetchComplettionCallback(false);
            }
        });
    }
    //endregion

    public boolean isInitialDataFetchComplete() {
        return initialDataFetchCompletionCount == 5;
    }

    public void invokeInitialDataFetchComplettionCallback(boolean success) {
        initialDataFetchCompletionCount ++;

        if (isInitialDataFetchComplete() && initialDataFetchCompletionCallBack != null) {
            if(success) {
                initialDataFetchCompletionCallBack.onSuccess(null);
            } else {
                initialDataFetchCompletionCallBack.onFailure();
            }

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
            if(b.closingDate == null || getMonth(b.closingDate) == Calendar.getInstance().get(Calendar.MONTH)) {
                totalRent += Integer.parseInt(b.rentAmount);
            }
        }

        return String.valueOf(totalRent);
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
                DataModel.Tenant tenant = getTenantInfoForBooking(pendingEntry.bookingId);
                if(tenant.mobile.isEmpty() == false) {
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

    public void createPendingEntryForBooking(String bookingId, DataModel.PendingType type, String amount, int month, final NetworkDataModulCallback<DataModel.Pending> callback) {

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
                // Also check if last month there has been an advance Receipt for this Booking
                if(r.id.equals(booking.id) && r.type == DataModel.ReceiptType.ADVANCE &&
                    getMonth(r.date) + 1 == Calendar.getInstance().get(Calendar.MONTH) + 2 ) {
                    rentAmount -= Integer.parseInt(r.cashAmount) + Integer.parseInt(r.onlineAmount);
                }
            }
            createPendingEntryForBooking(booking.id, DataModel.PendingType.RENT, String.valueOf(rentAmount), Calendar.getInstance().get(Calendar.MONTH) + 1, callback);

            if(smsPermission) {
                DataModel.Tenant tenant = getTenantInfoForBooking(booking.id);
                if (tenant.mobile.isEmpty() == false) {
                    SMSManagement smsManagement = SMSManagement.getInstance();

                    smsManagement.sendSMS(tenant.mobile,
                            smsManagement.getInstance().getSMSMessage(booking.id,
                                    tenant,
                                    0,
                                    SMSManagement.SMS_TYPE.MONTHLY_RENT)
                    );
                }
            }
        }
    }

    public void updatePendingEntry(String id, String amount, NetworkDataModulCallback<DataModel.Pending> callback) {
        DataModel.Pending pending = getPendingEntryByID(id);
        int totalPendingAmount = pending.pendingAmt;

        totalPendingAmount -= Integer.parseInt(amount);
        if (totalPendingAmount <= 0) {
            Call <ResponseBody> pendingCall = pendingService.deletePendingEntry(Integer.parseInt(pending.id));
            pendingCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (callback != null) {
                        pendingList.remove(pending);
                        callback.onSuccess(null);
                        callback.onResult();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
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
                        pendingList.set(pendingList.indexOf(pending),response.body());
                        callback.onSuccess(response.body());
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

    public void updatePendingEntryForBooking(String id, DataModel.PendingType type, String amount, NetworkDataModulCallback<DataModel.Pending> callback) {

        for (DataModel.Pending p: pendingList) {
            if(p.bookingId.equals(id) && p.type == type) {
                updatePendingEntry(p.id, amount, callback);
                break;
            }
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

    public void addNewBed(String bedNumber, String rent, String deposit, final NetworkDataModulCallback<DataModel.Bed> callback) {

        DataModel.Bed room = new DataModel.Bed(bedNumber, null, rent, deposit, false);
        addNewBed(room, callback);
    }

    public void updateBed(DataModel.Bed bedInfo, final NetworkDataModulCallback<DataModel.Bed> callback) {
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

    public void addNewTenant(String name, String mobile, String email, String address, String parentId, boolean isCurrent, final NetworkDataModulCallback<DataModel.Tenant> callback) {
        DataModel.Tenant newTenant = new DataModel.Tenant(null, name, mobile, email, address, isCurrent, parentId);
        Call<DataModel.Tenant> addTenantCall = tenantService.addTenant(newTenant);

        addTenantCall.enqueue(new Callback<DataModel.Tenant>() {
            @Override
            public void onResponse(Call<DataModel.Tenant> call, Response<DataModel.Tenant> response) {
                DataModel.Tenant newTenant = (DataModel.Tenant) response.body();
                tenantsList.add(0, newTenant);
                if(callback != null) {
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

    public void updateTenant(DataModel.Tenant tenant, NetworkDataModulCallback<DataModel.Tenant> callback) {
        updateTenant(tenant.id, tenant.name, tenant.mobile, "", tenant.email, tenant.address, tenant.isCurrent, tenant.parentId, callback);
    }

    public void updateTenant(String id, String name, String mobile, String mobile2, String email,
                             String address, Boolean isCurrent, @NotNull String parentId,
                             final NetworkDataModulCallback<DataModel.Tenant> callback) {
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
                DataModel.Tenant tenant = (DataModel.Tenant) response.body();
                for (DataModel.Tenant t : tenantsList) {
                    if (t.id.equals(tenant.id)) {
                        tenantsList.set(tenantsList.indexOf(t), tenant);
                        if(callback != null) {
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
                                 final NetworkDataModulCallback<DataModel.Booking> callback) {
        DataModel.Booking booking = new DataModel.Booking(null, bedNumber, rent, deposit, admissionDate, true, tenantId, null);
        Call<DataModel.Booking> createBookingCall = bookingService.createBooking(booking);

        createBookingCall.enqueue(new Callback<DataModel.Booking>() {
            @Override
            public void onResponse(Call<DataModel.Booking> call, Response<DataModel.Booking> response) {
                Log.i(TAG, "Create Booking Successful");
                DataModel.Booking newBooking = response.body();
                bookingsList.add(0, newBooking);
                DataModel.Bed bed = getBedInfo(newBooking.bedNumber);
                bed.isOccupied = true;
                bed.bookingId = newBooking.id;
                updateBed(bed, null);

                DataModel.Tenant tenant = getTenantInfoForBooking(newBooking.id);
                tenant.isCurrent = true;
                updateTenant(tenant, null);

                if(callback != null) {
                    callback.onSuccess(response.body());
                    callback.onResult();
                }
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
                            callback.onResult();
                        }
                        break;
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

                    if (!amt.isEmpty())
                        updatePendingEntryForBooking(booking.id, p.type, amt, null);
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
                updateBed(bed, null);

                DataModel.Tenant tenant = getTenantInfoForBooking(booking.id);
                tenant.isCurrent = false;
                tenant.parentId = "0";
                updateTenant(tenant, null);
                for (DataModel.Tenant t: getDependents(tenant.id)){
                    tenant.isCurrent = false;
                    tenant.parentId = "0";
                    updateTenant(tenant, null);
                }

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
                    callback.onResult();
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

    public void createReceiptForPendingEntry(String id, String onlineAmount, String cashAmount, boolean penaltyWaiveOff, NetworkDataModulCallback<DataModel.Receipt> callback) {
        DataModel.Pending pendingEntry = getPendingEntryByID(id);

        createReceipt(DataModel.ReceiptType.values()[pendingEntry.type.getIntValue()], pendingEntry.bookingId, onlineAmount, cashAmount, penaltyWaiveOff,
                new NetworkDataModulCallback<DataModel.Receipt>() {
                    @Override
                    public void onSuccess(DataModel.Receipt newReceipt) {
                        updatePendingEntry(pendingEntry.id, String.valueOf(pendingEntry.pendingAmt),
                                new NetworkDataModulCallback<DataModel.Pending>() {
                                    @Override
                                    public void onSuccess(DataModel.Pending obj) {
                                        if(callback != null) {
                                            callback.onSuccess(newReceipt);
                                            callback.onResult();
                                        }
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

                    @Override
                    public void onFailure() {
                        if(callback != null) {
                            callback.onFailure();
                            callback.onResult();
                        }
                    }
        });
    }

    public void createReceipt(DataModel.ReceiptType type, String bookingId, String onlineAmount, String cashAmount, boolean penaltyWaiveOff, NetworkDataModulCallback<DataModel.Receipt> callback) {
        DataModel.Receipt newReceipt = new DataModel.Receipt(null, bookingId, onlineAmount, cashAmount, penaltyWaiveOff, new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString(), type);
        Call<DataModel.Receipt> createReceiptCall = receiptsService.createReceipt(newReceipt);

        createReceiptCall.enqueue(new Callback<DataModel.Receipt>() {
            @Override
            public void onResponse(Call<DataModel.Receipt> call, Response<DataModel.Receipt> response) {
                Log.i(TAG, "Create Receipt Successful");
                receiptsList.add(0, response.body());
                if(callback != null) {
                    callback.onSuccess(response.body());
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
        try { c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(fromDate)); }
        catch (ParseException e) { e.printStackTrace(); }

        Log.i(TAG, "getMonth : " + c.get(Calendar.MONTH));
        return c.get(Calendar.MONTH) + 1;
    }
}

class Room {

}