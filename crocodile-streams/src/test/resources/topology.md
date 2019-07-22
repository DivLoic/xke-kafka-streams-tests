```
Topologies:
  Sub-topology: 0
    Source: KSTREAM-SOURCE-0000000012 (topics: [KSTREAM-FILTER-0000000009-repartition])
                                                 --> KSTREAM-JOIN-0000000013
    Processor: KSTREAM-JOIN-0000000013 (stores: [CROCO-LINKS-STATE-STORE-0000000000])
                                                  --> KSTREAM-WINDOWED-0000000014
                                                  <-- KSTREAM-SOURCE-0000000012
    Source: KSTREAM-SOURCE-0000000004 (topics: [CROCO-PURCHASES])
                                                 --> KSTREAM-WINDOWED-0000000015
    Processor: KSTREAM-WINDOWED-0000000014 (stores: [KSTREAM-JOINTHIS-0000000016-store])
                                                      --> KSTREAM-JOINTHIS-0000000016
                                                      <-- KSTREAM-JOIN-0000000013
    Processor: KSTREAM-WINDOWED-0000000015 (stores: [KSTREAM-JOINOTHER-0000000017-store])
                                                      --> KSTREAM-JOINOTHER-0000000017
                                                      <-- KSTREAM-SOURCE-0000000004
    Processor: KSTREAM-JOINOTHER-0000000017 (stores: [KSTREAM-JOINTHIS-0000000016-store])
                                                       --> KSTREAM-MERGE-0000000018
                                                       <-- KSTREAM-WINDOWED-0000000015
    Processor: KSTREAM-JOINTHIS-0000000016 (stores: [KSTREAM-JOINOTHER-0000000017-store])
                                                      --> KSTREAM-MERGE-0000000018
                                                      <-- KSTREAM-WINDOWED-0000000014
    Processor: KSTREAM-MERGE-0000000018 (stores: [])
                                                   --> KSTREAM-TRANSFORM-0000000019
                                                   <-- KSTREAM-JOINTHIS-0000000016, KSTREAM-JOINOTHER-0000000017
    Source: KSTREAM-SOURCE-0000000001 (topics: [CROCO-LINKS])
                                                 --> KTABLE-SOURCE-0000000002
    Processor: KSTREAM-TRANSFORM-0000000019 (stores: [])
                                                       --> KSTREAM-SINK-0000000020
                                                       <-- KSTREAM-MERGE-0000000018
    Sink: KSTREAM-SINK-0000000020 (topic: CROCO-VOUCHERS)
            <-- KSTREAM-TRANSFORM-0000000019
    Processor: KTABLE-SOURCE-0000000002 (stores: [CROCO-LINKS-STATE-STORE-0000000000])
                                                   --> none
                                                   <-- KSTREAM-SOURCE-0000000001

  Sub-topology: 1
    Source: KSTREAM-SOURCE-0000000003 (topics: [CROCO-CLICKS])
                                                 --> KSTREAM-MAP-0000000007
    Processor: KSTREAM-MAP-0000000007 (stores: [])
                                                 --> KSTREAM-FILTER-0000000008
                                                 <-- KSTREAM-SOURCE-0000000003
    Processor: KSTREAM-FILTER-0000000008 (stores: [])
                                                    --> KSTREAM-FILTER-0000000009
                                                    <-- KSTREAM-MAP-0000000007
    Processor: KSTREAM-FILTER-0000000009 (stores: [])
                                                    --> KSTREAM-FILTER-0000000011
                                                    <-- KSTREAM-FILTER-0000000008
    Processor: KSTREAM-FILTER-0000000011 (stores: [])
                                                    --> KSTREAM-SINK-0000000010
                                                    <-- KSTREAM-FILTER-0000000009
    Sink: KSTREAM-SINK-0000000010 (topic: KSTREAM-FILTER-0000000009-repartition)
            <-- KSTREAM-FILTER-0000000011

  Sub-topology: 2 for global store (will not generate tasks)
    Source: KTABLE-SOURCE-0000000005 (topics: [CROCO-COUPONS])
                                                --> KTABLE-SOURCE-0000000006
    Processor: KTABLE-SOURCE-0000000006 (stores: [CROCO-STORE-COUPON])
                                                   --> none
                                                   <-- KTABLE-SOURCE-0000000005
```