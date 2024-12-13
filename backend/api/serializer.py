from rest_framework import serializers
from .models import Medics, Sessions, Client, Hospital, Pharmacy

from rest_framework import serializers
from .models import Medics


class MedicsSerializer(serializers.ModelSerializer):
    class Meta:
        model = Medics
        fields = ('id', 'medic_name', 'speciality', 'medic_image', 'price', 'hospital', 'favorites')


class SessionsSerializer(serializers.ModelSerializer):
    medics = MedicsSerializer()

    class Meta:
        model = Sessions
        fields = ('id', 'appointment', 'medics', 'client')


class ClientSerializer(serializers.ModelSerializer):
    class Meta:
        model = Client
        fields = ('id', 'client_name')


class HospitalSerializer(serializers.ModelSerializer):
    class Meta:
        model = Hospital
        fields = ('id', 'name', 'street_address', "city", "bed_count", "image_url", "rating", "favorites")


class PharmacySerializer(serializers.ModelSerializer):
    class Meta:
        model = Pharmacy
        fields = ('id', 'name', 'address', 'image_url', 'rating', 'phone_number', 'email')
