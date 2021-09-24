package com.fandataxiuser.data.network.model;

import com.google.gson.annotations.SerializedName;

public class DeliveryData{

	@SerializedName("delivery_address")
	private String deliveryAddress;

	@SerializedName("receiver_name")
	private String receiverName;

	@SerializedName("item_to_deliver")
	private String itemToDeliver;

	@SerializedName("receiver_mobile")
	private String receiverMobile;

	@SerializedName("any_instructions")
	private String anyInstructions;

	public void setDeliveryAddress(String deliveryAddress){
		this.deliveryAddress = deliveryAddress;
	}

	public String getDeliveryAddress(){
		return deliveryAddress;
	}

	public void setReceiverName(String receiverName){
		this.receiverName = receiverName;
	}

	public String getReceiverName(){
		return receiverName;
	}

	public void setItemToDeliver(String itemToDeliver){
		this.itemToDeliver = itemToDeliver;
	}

	public String getItemToDeliver(){
		return itemToDeliver;
	}

	public void setReceiverMobile(String receiverMobile){
		this.receiverMobile = receiverMobile;
	}

	public String getReceiverMobile(){
		return receiverMobile;
	}

	public void setAnyInstructions(String anyInstructions){
		this.anyInstructions = anyInstructions;
	}

	public String getAnyInstructions(){
		return anyInstructions;
	}

	@Override
 	public String toString(){
		return 
			"DeliveryData{" + 
			"delivery_address = '" + deliveryAddress + '\'' + 
			",receiver_name = '" + receiverName + '\'' + 
			",item_to_deliver = '" + itemToDeliver + '\'' + 
			",receiver_mobile = '" + receiverMobile + '\'' + 
			",any_instructions = '" + anyInstructions + '\'' + 
			"}";
		}
}