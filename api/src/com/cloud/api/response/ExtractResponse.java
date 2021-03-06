// Copyright 2012 Citrix Systems, Inc. Licensed under the
// Apache License, Version 2.0 (the "License"); you may not use this
// file except in compliance with the License.  Citrix Systems, Inc.
// reserves all rights not expressly granted by the License.
// You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
// Automatically generated by addcopyright.py at 04/03/2012
package com.cloud.api.response;

import java.util.Date;

import com.cloud.api.ApiConstants;
import com.cloud.utils.IdentityProxy;
import com.cloud.serializer.Param;
import com.google.gson.annotations.SerializedName;

public class ExtractResponse extends BaseResponse {
    @SerializedName(ApiConstants.ID) @Param(description="the id of extracted object")
    private IdentityProxy id = new IdentityProxy("vm_template");
    
    @SerializedName(ApiConstants.NAME) @Param(description="the name of the extracted object")
    private String name;
    
    @SerializedName("extractId") @Param(description="the upload id of extracted object")
    private IdentityProxy uploadId = new IdentityProxy("async_job");
    
    @SerializedName("uploadpercentage") @Param(description="the percentage of the entity uploaded to the specified location")
    private Integer uploadPercent;
        
    @SerializedName("status") @Param(description="the status of the extraction")
    private String status;
    
    @SerializedName("accountid") @Param(description="the account id to which the extracted object belongs")
    private IdentityProxy accountId = new IdentityProxy("account");    
 
    @SerializedName("resultstring") @Param(includeInApiDoc=false)
    private String resultString;    

    @SerializedName(ApiConstants.CREATED) @Param(description="the time and date the object was created")
    private Date createdDate;

    @SerializedName(ApiConstants.STATE) @Param(description="the state of the extracted object")
    private String state;
    
    @SerializedName("storagetype") @Param(description="type of the storage")
    private String storageType;

    @SerializedName("storage")
    private String storage;
    
    @SerializedName(ApiConstants.ZONE_ID) @Param(description="zone ID the object was extracted from")
    private IdentityProxy zoneId = new IdentityProxy("data_center");

    @SerializedName(ApiConstants.ZONE_NAME) @Param(description="zone name the object was extracted from")
    private String zoneName;

    @SerializedName("extractMode") @Param(description="the mode of extraction - upload or download")
    private String mode;
    
    @SerializedName(ApiConstants.URL) @Param(description="if mode = upload then url of the uploaded entity. if mode = download the url from which the entity can be downloaded")
    private String url;   
    
    public ExtractResponse(){        
    }
    
    public ExtractResponse(Long typeId, String typeName, long accountId,
            String state, Long uploadId) {
        this.id.setValue(typeId);
        this.name = typeName;
        this.accountId.setValue(accountId);
        this.state = state;
        this.uploadId.setValue(uploadId);        
    }

    public Long getId() {
        return id.getValue();
    }

    public void setId(long id) {
        this.id.setValue(id);
    }
    
    public void setIdentityTableName(String tableName) {
    	this.id.setTableName(tableName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUploadId() {
        return uploadId.getValue();
    }

    public void setUploadId(Long uploadId) {
        this.uploadId.setValue(uploadId);
    }

    public Integer getUploadPercent() {
        return uploadPercent;
    }

    public void setUploadPercent(int uploadPercent) {
        this.uploadPercent = uploadPercent;
    }

    public String getUploadStatus() {
        return status;
    }

    public void setUploadStatus(String status) {
        this.status = status;
    }

    public Long getAccountId() {
        return accountId.getValue();
    }

    public void setAccountId(long accountId) {
        this.accountId.setValue(accountId);
    }

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public Long getZoneId() {
        return zoneId.getValue();
    }

    public void setZoneId(Long zoneId) {
        this.zoneId.setValue(zoneId);
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
