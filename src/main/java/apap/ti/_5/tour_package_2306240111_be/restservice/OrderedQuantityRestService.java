package apap.ti._5.tour_package_2306240111_be.restservice;

import apap.ti._5.tour_package_2306240111_be.model.Activity;
import apap.ti._5.tour_package_2306240111_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity.CreateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306240111_be.restdto.request.orderedquantity.UpdateOrderedQuantityRequestDTO;

import java.util.List;
import java.util.UUID;

public interface OrderedQuantityRestService {
    
    OrderedQuantity createOrderedQuantity(CreateOrderedQuantityRequestDTO requestDTO);
    
    OrderedQuantity getOrderedQuantityById(UUID id);
    
    OrderedQuantity updateOrderedQuantity(UUID id, UpdateOrderedQuantityRequestDTO requestDTO);
    
    void deleteOrderedQuantity(UUID id);
    
    List<Activity> getAllActivities();
    
    List<Activity> getEligibleActivities(UUID planId);
}