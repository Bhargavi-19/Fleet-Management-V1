package com.example.demo.enums;

/**
 * Which end of a booking a staff member is looking at.
 *
 * A rental can be one-way: collected at BOM Hub and returned to Nagpur Hub.
 * That single booking belongs to two different hubs at two different moments,
 * so every staff query has to say which end it means.
 *
 *   PICKUP  - bookings this hub HANDS OVER   (pickup_hub_id  = my hub)
 *   RETURN  - bookings this hub TAKES BACK   (dropoff_hub_id = my hub)
 *   ALL     - anything this hub is involved in, either end
 *
 * So for a BOM -> Nagpur booking:
 *   BOM staff    see it under PICKUP  (Hand-over module)
 *   Nagpur staff see it under RETURN  (Return module)
 *   Neither sees it in the other's module.
 */
public enum HubScope {

    PICKUP,
    RETURN,
    ALL
}
