from django.contrib import admin
from .models import Medics, Sessions, Client, Hospital, Pharmacy


@admin.register(Medics)
class MedicsAdmin(admin.ModelAdmin):
    list_display = ('id', 'medic_name', 'speciality')


@admin.register(Sessions)
class SessionsAdmin(admin.ModelAdmin):
    list_display = ('id', 'medics_id', 'appointment', 'client_id')


@admin.register(Client)
class ClientAdmin(admin.ModelAdmin):
    list_display = ('id', 'client_name')


@admin.register(Hospital)
class HospitalAdmin(admin.ModelAdmin):
    list_display = ('id', 'name', 'street_address', "city", "bed_count", "image_url", "rating")


@admin.register(Pharmacy)
class PharmacyAdmin(admin.ModelAdmin):
    list_display = ('id', 'name', 'address', 'image_url', 'rating', 'phone_number', 'email')
