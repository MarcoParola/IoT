CONTIKI_PROJECT = music_node
all: $(CONTIKI_PROJECT)


# Include CoAP resources
MODULES_REL += ./resources

CONTIKI=../../../../..

# Include the CoAP implementation
include $(CONTIKI)/Makefile.dir-variables
MODULES += $(CONTIKI_NG_APP_LAYER_DIR)/coap

include $(CONTIKI)/Makefile.include
