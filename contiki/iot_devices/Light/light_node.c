#include "coap-blocking-api.h"
#include "coap-engine.h"
#include "contiki-net.h"
#include "contiki.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* Log configuration */
#include "sys/log.h"

#define LOG_MODULE "Light node"
#define LOG_LEVEL LOG_LEVEL_DBG

#define SERVER_EP ("coap://[fd00::1]:5683")
#define SERVER_REGISTRATION ("/registration")

extern coap_resource_t res_light;
extern coap_resource_t res_bulbSwitch;
extern coap_resource_t res_temperature;

static coap_message_type_t result = COAP_TYPE_RST;

PROCESS(light_node, "Light Node");
AUTOSTART_PROCESSES(&light_node);

static void response_handler(coap_message_t *response) {
    if (response == NULL)
        return;
    LOG_DBG("Response %i\n", response->type);
    result = response->type;
}

PROCESS_THREAD(light_node, ev, data) {

    static coap_endpoint_t server_ep;
    static coap_message_t request[1];

    PROCESS_BEGIN();

    LOG_INFO("Starting light node\n");

    coap_activate_resource(&res_light, "sensors/light");
	coap_activate_resource(&res_bulbSwitch, "actuator/light");

    coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

    do {
        coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
        coap_set_header_uri_path(request, (const char *)&SERVER_REGISTRATION);
        COAP_BLOCKING_REQUEST(&server_ep, request, response_handler);
    } while (result == COAP_TYPE_RST);

    PROCESS_END();
}
