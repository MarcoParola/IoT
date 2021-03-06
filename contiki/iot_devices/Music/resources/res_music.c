#include "contiki.h"
#include "coap-engine.h"
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "sys/log.h"
#include "os/dev/leds.h"

#define LOG_MODULE "Music actuator"
#define LOG_LEVEL LOG_LEVEL_DBG

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer,uint16_t preferred_size,int32_t *offset);

static void res_post_put_handler(coap_message_t *request,coap_message_t *response,
				uint8_t *buffer,uint16_t preferred_size, int32_t *offset);


int res_mode = 0;

RESOURCE(res_music,
         "title=\"Music actuator\";methods=\"GET/PUT/POST\", mode=OFF|ON\";rt=\"int\"\n",
         res_get_handler,
	 res_post_put_handler,
	 res_post_put_handler,
	 NULL);


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size,int32_t *offset) {

    unsigned int accept = -1;
    if (!coap_get_header_accept(request, &accept))
        accept = APPLICATION_JSON;

    if (accept == APPLICATION_JSON) {
		char *res_mode_str = NULL;

		switch(res_mode){
			case 0:
				res_mode_str = "OFF";
				break;
			case 1:
				res_mode_str = "ON";
				break;
		}

		snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"mode\":\"%s\"}",res_mode_str);
		coap_set_header_content_format(response, APPLICATION_JSON);
		coap_set_payload(response, buffer, strlen((char *)buffer));

    } else {
        coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
        const char *msg = "Supporting content-type application/json";
        coap_set_payload(response, msg, strlen(msg));
    }
}


static void res_post_put_handler(coap_message_t *request,coap_message_t *response, uint8_t *buffer,uint16_t preferred_size, int32_t *offset) {
    size_t len = 0;
    const char *value = NULL;
    int success = 1;

    if ((len = coap_get_post_variable(request,"\"mode\"",&value))) {
        if (strncmp(value, "\"OFF\"", len) == 0) {
            res_mode = 0;
			leds_single_on(LEDS_RED);
			leds_single_off(LEDS_GREEN);
        } else if (strncmp(value, "\"ON\"", len) == 0) {
            res_mode = 1;
			leds_single_off(LEDS_RED);
			leds_single_on(LEDS_GREEN);
        }else
            success = 0;
    } else
        success = 0;
    
    if (!success) 
        coap_set_status_code(response, BAD_REQUEST_4_00);
    else
    	coap_set_status_code(response, CHANGED_2_04);
}

