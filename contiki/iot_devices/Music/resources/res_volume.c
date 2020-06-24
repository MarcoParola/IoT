#include "contiki.h"
#include "coap-engine.h"
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "sys/log.h"

#define LOG_MODULE "Music actuator"
#define LOG_LEVEL LOG_LEVEL_DBG
#define max_volume 100
#define min_volume 0

static void res_get_handler(coap_message_t *request, coap_message_t *response,
                            uint8_t *buffer,uint16_t preferred_size,int32_t *offset);

static void res_post_put_handler(coap_message_t *request,coap_message_t *response,
				uint8_t *buffer,uint16_t preferred_size, int32_t *offset);

extern int res_mode;
static int volume = 20;

RESOURCE(res_volume,
         "title=\"Volume actuator\";methods=\"GET/PUT/POST\",\";rt=\"int\"\n",
         res_get_handler,
	 res_post_put_handler,
	 res_post_put_handler,
	 NULL);


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size,int32_t *offset) {

	LOG_INFO("Volume");
    unsigned int accept = -1;
    if (!coap_get_header_accept(request, &accept))
        accept = APPLICATION_JSON;

    if (accept == APPLICATION_JSON) {

	snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"volume\":\"%d\"}",volume);
	coap_set_header_content_format(response, APPLICATION_JSON);
	coap_set_payload(response, buffer, strlen((char *)buffer));

    } else {
        coap_set_status_code(response, NOT_ACCEPTABLE_4_06);
        const char *msg = "Supporting content-type application/json";
        coap_set_payload(response, msg, strlen(msg));
    }
}


static void res_post_put_handler(coap_message_t *request,coap_message_t *response, uint8_t *buffer,uint16_t preferred_size, int32_t *offset) {

	LOG_INFO("Volume");
	if(res_mode == 1){
		LOG_INFO("Volume");
	    size_t len = 0;
	    const char *value = NULL;
		int vol;
	    int success = 1;
		

	    if ((len = coap_get_post_variable(request,"\"volume\"",&value))) {
			/*volume++;
			if(strncmp(value, "60", len) == 0)
				volume = 60;*/
			vol = atoi(value);
			LOG_DBG("Volume: %d %s", vol, value);
			if (vol < min_volume || vol > max_volume) 
				success = 0;
			 else 
				volume = vol;
			} 
		else
			success = 0;
	    
	    if (!success) 
			coap_set_status_code(response, BAD_REQUEST_4_00);
	    else
	    	coap_set_status_code(response, CHANGED_2_04);
	}
	else if(res_mode == 0)
		coap_set_status_code(response, BAD_REQUEST_4_00);
}

