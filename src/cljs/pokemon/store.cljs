(ns pokemon.store
  (:require
   [reagent.core :as reagent :refer [atom]]))

(defonce initial-state
  {:types {}
   :search ""
   :sorting :popularity
   :unavailable-pokemon
   #{896 928 960 641 897 929
     961 642 898 930 962 899
     931 963 900 932 964 421
     741 901 933 965 774 902
     934 966 487 647 903 935
     967 648 904 936 968 585
     745 905 937 969 586 746
     778 906 938 970 875 907
     939 971 492 908 940 972
     877 909 941 973 718 910
     942 974 911 943 975 720
     912 944 976 849 913 945
     977 914 946 978 915 947
     979 916 948 980 917 949
     918 950 919 951 888 920
     952 889 921 953 922 954
     891 923 955 412 892 924
     956 413 893 925 957 894
     926 958 895 927 959}
   :pokemon {:index true
             :cart true}
   :pokemon-ids #{}
   :pokemon-hash {}
   :pokemon-details {}
   :cart #{}
   :purchase-stage :buy
   :select-store nil})
(defonce store (atom initial-state))
