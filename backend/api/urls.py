from django.urls import path
from . import views

urlpatterns = [
    path('medics_all/', views.medics_all_list, name='medics_all_list'),
    path('medics/', views.medics_list, name='medics_list'),

    path('sessions/', views.sessions_list, name='sessions_list'),
    path('sessions/<int:pk>/', views.sessions_detail, name='sessions_detail'),

    path('clients/', views.client_list, name='client_list'),
    path('clients/<int:pk>/', views.client_detail, name='client_detail'),

    path('hospitals/', views.hospital_list, name='hospital_list'),
    path('pharmacy/', views.pharmacy_list, name='pharmacy_list'),

    path('add_favorite_medic/', views.add_favorite_medic, name='add_favorite_medic'),
    path('remove_favorite_medic/', views.remove_favorite_medic, name='remove_favorite_medic'),
    path('favorite_medics/<int:client_id>/', views.list_favorite_medics, name='list_favorite_medics'),

    path('add_favorite/', views.add_favorite_hospital, name='add_favorite_hospital'),
    path('remove_favorite_hospital/', views.remove_favorite_hospital, name='remove_favorite_hospital'),
    path('favorite_hospitals/<int:client_id>/', views.list_favorite_hospitals, name='list_favorite_hospitals'),
]
