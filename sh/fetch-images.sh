for i in $(seq 1 890)
do
  wget -P src/images  https://pokeres.bastionbot.org/images/pokemon/"$i".png
done

cd src/images
mogrify -format png -thumbnail 120x100  *
