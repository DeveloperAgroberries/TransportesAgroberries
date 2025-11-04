package com.AgroberriesMX.transportesagroberries.data.network

import android.content.Context
import com.AgroberriesMX.transportesagroberries.BuildConfig.BASE_URL
import com.AgroberriesMX.transportesagroberries.data.RepositoryImpl
import com.AgroberriesMX.transportesagroberries.data.core.interceptors.AuthInterceptor
import com.AgroberriesMX.transportesagroberries.data.local.DatabaseHelper
import com.AgroberriesMX.transportesagroberries.data.repository.LocalAuthRepository
import com.AgroberriesMX.transportesagroberries.domain.Repository
import com.AgroberriesMX.transportesagroberries.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient):Retrofit{
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //PARA HACER PRUEBAS Y VER COMO SE MANDA EL OBJETO A LA API
   /* @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // El nivel BODY es el más útil para depurar, ya que muestra el cuerpo de la petición y la respuesta.
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient
            .Builder()
            // Agrega el interceptor de logging para que se registren todas las peticiones
            .addInterceptor(loggingInterceptor)
            // Agrega el interceptor de autorización para incluir el token
            .addInterceptor(authInterceptor)
            .build()
    }*/
    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient{
        return OkHttpClient
            .Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    fun provideTransportApiService(retrofit: Retrofit):TransportApiService{
        return retrofit.create(TransportApiService::class.java)
    }

    // BORRA LA FUNCIÓN DE DATABASEHELPER DE ESTE MÓDULO.
    // Dagger la encontrará en tu otro DatabaseModule.

    @Provides
    fun provideTransportRepository(
        transportApiService: TransportApiService,
        dbHelper: DatabaseHelper
    ): Repository {
        return RepositoryImpl(transportApiService, dbHelper)
    }

    @Provides
    @Singleton
    fun provideNetworkUtils(@ApplicationContext context: Context): NetworkUtils {
        return NetworkUtils(context)
    }

    @Provides
    @Singleton
    fun provideLocalAuthRepository(@ApplicationContext context: Context): LocalAuthRepository {
        return LocalAuthRepository(context)
    }
}