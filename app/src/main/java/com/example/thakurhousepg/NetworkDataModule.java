package com.example.thakurhousepg;


import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
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


interface BedsService {
    @GET("rooms")
    Call<List<DataModel.Bed>> listRooms();

    @PUT("rooms/{number}")
    Call<ResponseBody> addRoom(@Path("number") String roomNumber, @Body DataModel.Bed room);

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
    Call<DataModel.Booking> createBooking(@Body DataModel.Booking tenant);
}

interface PendingService {
    @GET("pending")
    Call<List<DataModel.Pending>> getAllPendingEntries();

    @POST("pending/{id}")
    Call<DataModel.Pending> updatePendingEntry(@Path("id") String id, @Body DataModel.Pending pendingEntryInfo);

    @PUT("/pending")
    Call<DataModel.Pending> createPendingEntry(@Body DataModel.Pending pendingEntry);
}

public class NetworkDataModule {
    private static final String TAG = "NetworkDataModule";
    private static NetworkDataModule _instance = null;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    ArrayList<DataModel.Bed> bedsList = new ArrayList<DataModel.Bed>();
    ArrayList<DataModel.Tenant> tenantsList = new ArrayList<DataModel.Tenant>();
    ArrayList<DataModel.Booking> bookingsList = new ArrayList<DataModel.Booking>();
    ArrayList<DataModel.Pending> pendingList = new ArrayList<DataModel.Pending>();

    BedsService service = retrofit.create(BedsService.class);
    TenantService tenantService = retrofit.create(TenantService.class);
    BookingService bookingService = retrofit.create(BookingService.class);
    PendingService pendingService = retrofit.create(PendingService.class);

    public static NetworkDataModule getInstance() {
        if(_instance == null) {
            _instance = new NetworkDataModule();
        }

        return _instance;
    }

    public NetworkDataModule() {
        init();
    }

    public void init() {
        retrieveBedsList();
        retrieveTenantsList();
        retrieveBookings();
        retrievePendingEntries();
    }

    private void retrieveBedsList() {
        Call<List<DataModel.Bed>> roomsCall = service.listRooms();

        roomsCall.enqueue(new Callback<List<DataModel.Bed>>() {
            @Override
            public void onResponse(Call<List<DataModel.Bed>> call, Response<List<DataModel.Bed>> response) {
                bedsList.clear();
                bedsList.addAll(response.body());
                for(DataModel.Bed bed:bedsList) {
                    Log.i(TAG, bed.toString());
                }
            }

            @Override
            public void onFailure(Call<List<DataModel.Bed>> call, Throwable t) {
                Log.i(TAG, "Call to Rest Service failed: " + call.request());
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
                for(DataModel.Booking booking:bookingsList) {
                    Log.i(TAG, booking.toString());
                }
            }

            @Override
            public void onFailure(Call<List<DataModel.Booking>> call, Throwable t) {
                Log.i(TAG, "Call to Rest Service failed: " + call.request());
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
                for(DataModel.Tenant tenant:tenantsList) {
                    Log.i(TAG, tenant.toString());
                }
            }

            @Override
            public void onFailure(Call<List<DataModel.Tenant>> call, Throwable t) {
                Log.i(TAG, "Call to Tenant Service failed: " + call.request());
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
                for(DataModel.Pending pending:pendingList) {
                    Log.i(TAG, pending.toString());
                }
            }

            @Override
            public void onFailure(Call<List<DataModel.Pending>> call, Throwable t) {
                Log.i(TAG, "Call to Tenant Service failed: " + call.request());
            }
        });
    }

    public ArrayList<DataModel.Bed> getBedsList() {
        return bedsList;
    }

    public ArrayList<DataModel.Tenant> getAllTenants(boolean onlyCurrent) {
        ArrayList<DataModel.Tenant> list = new ArrayList<DataModel.Tenant>();
        if (onlyCurrent) {
            for(DataModel.Tenant t:tenantsList) {
                if(t.isCurrent)
                    list.add(t);
            }
        } else {
            list.addAll(tenantsList);
        }
        return list;
    }

    public DataModel.Booking getBookingInfo(String id) {
        DataModel.Booking booking = null;
        for(DataModel.Booking b:bookingsList) {
            if(b.id.equals(id)) {
                booking = b;
                break;
            }
        }

        return booking;
    }

    public DataModel.Tenant getTenantInfoForBooking(String id) {
        DataModel.Booking b = getBookingInfo(id);
        DataModel.Tenant tenant = null;
        if(b!= null) {
            for(DataModel.Tenant t:tenantsList) {
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

    public void addNewBed(String bedNumber, String rent, String deposit) {
        DataModel.Bed room = new DataModel.Bed(bedNumber, null, rent, deposit, false);
        Call<ResponseBody> addRoomCall = service.addRoom(bedNumber, room);

        addRoomCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "Add Room Successful, Received Response Body: " + response.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "Call to Add Room Service failed: " + call.request());
            }
        });
    }

    public void addNewTenant(String name, String mobile, String email, String address, String parentId, boolean isCurrent) {
        DataModel.Tenant newTenant = new DataModel.Tenant(null, name, mobile, email, address, isCurrent, parentId);
        Call<DataModel.Tenant> addTenantCall = tenantService.addTenant(newTenant);

        addTenantCall.enqueue(new Callback<DataModel.Tenant>() {
            @Override
            public void onResponse(Call<DataModel.Tenant> call, Response<DataModel.Tenant> response) {
                DataModel.Tenant t = (DataModel.Tenant) response.body();
            }

            @Override
            public void onFailure(Call<DataModel.Tenant> call, Throwable t) {
                Log.i(TAG, "Add new tenant failed");
            }
        });
    }

    public void createDefaultBeds() {

        /* For Ground Floor */
        addNewBed("000.1", "4250", "4250");
        addNewBed("000.2", "4250", "4250");
        addNewBed("000.3", "4250", "4250");
        addNewBed("000.4", "4250", "4250");
        addNewBed("000.5", "4250", "4250");
        addNewBed("000.6", "4250", "4250");

        for (int floorNo = 100; floorNo <= 600; floorNo += 100) {
            int numOfRooms = 7;

            if(floorNo >= 200) {
                numOfRooms = 8;
            }
            for (int roomNo = 1; roomNo <= numOfRooms; roomNo += 1) {
                Double bedNumber = Double.valueOf(floorNo + roomNo);
                addNewBed(bedNumber.toString(), "8000", "8000");
            }
        }
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
        for(DataModel.Tenant t: tenantsList) {
            if (t.parentId.equals(id)) {
                dependentList.add(t);
            }
        }

        return dependentList;
    }

    public DataModel.Bed getBedInfo(String forBedNumber) {
        DataModel.Bed bed = null;
        for(DataModel.Bed b:bedsList) {
            if(b.bedNumber.equals(forBedNumber)) {
                bed = b;
                break;
            }
        }

        return bed;
    }

    public DataModel.Tenant getTenantInfo(String id) {
        DataModel.Tenant tenant = null;
        for(DataModel.Tenant t:tenantsList) {
            if(t.id.equals(id)) {
                tenant = t;
            }
        }

        return tenant;
    }

    public boolean updateTenant(String id, String name, String mobile, String mobile2, String email, String address, Boolean isCurrent, @NotNull String parentId) {
        DataModel.Tenant newTenant = new DataModel.Tenant(id, name, mobile, email, address, isCurrent, parentId);
        Call<DataModel.Tenant> updateTenantCall = tenantService.updateTenant(id, newTenant);

        updateTenantCall.enqueue(new Callback<DataModel.Tenant>() {
            @Override
            public void onResponse(Call<DataModel.Tenant> call, Response<DataModel.Tenant> response) {
                DataModel.Tenant tenant = (DataModel.Tenant) response.body();
                for (DataModel.Tenant t:tenantsList) {
                    if (t.id.equals(tenant.id)) {
                        tenantsList.set(tenantsList.indexOf(t), tenant);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<DataModel.Tenant> call, Throwable t) {
                Log.i(TAG, "Add new tenant failed");
            }
        });

        return true;
    }

}

class Room {

}